package org.imc.demo.auth.jwt.controllers;

import org.imc.demo.auth.jwt.exceptions.NotAuthorizedException;
import org.imc.demo.auth.jwt.models.User;
import org.imc.demo.auth.jwt.providers.TokenProvider;
import org.imc.demo.auth.jwt.providers.UserService;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TokenController {


    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    UserService userService;

    @CrossOrigin
    @RequestMapping(value = "/check", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Map<String, Object> validateToken(@RequestParam(name="token", required = true) String token) throws NotAuthorizedException {

        if (StringUtils.isEmpty(token) || token.split("\\.").length != 3) {
            throw new NotAuthorizedException("Missing token");
        }

        if (!tokenProvider.isKnownToken(token)) {
            throw new NotAuthorizedException("Invalid token");
        }

        return tokenProvider.unpackToken(token);

    }

    @CrossOrigin
    @RequestMapping(value = "/profile", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Map<String, String> getUserProfile(@RequestBody(required = true) Map<String, String> payload) throws NotAuthorizedException {
        if (payload.size() == 0 || !payload.containsKey("token")) {
            throw new NotAuthorizedException("Invalid payload");
        }

        String token = payload.get("token");

        if (!tokenProvider.isKnownToken(token)) {
            throw new NotAuthorizedException("Invalid token");
        }

        Map<String, Object> tokenPayload = tokenProvider.unpackToken(token);
        String userId = tokenPayload.get("sub").toString();
        User user = userService.getById(userId);

        Map<String, String> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("username", user.getUsername());

        return response;
    }


    @ExceptionHandler(value = {NotAuthorizedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public JSONObject handleNotAuthorized() {
        JSONObject retObj = new JSONObject();
        retObj.put("error", "Not Authorized");
        return retObj;
    }


}
