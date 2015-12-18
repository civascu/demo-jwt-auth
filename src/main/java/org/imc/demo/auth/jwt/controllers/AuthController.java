package org.imc.demo.auth.jwt.controllers;

import org.imc.demo.auth.jwt.exceptions.NotAuthorizedException;
import org.imc.demo.auth.jwt.models.User;
import org.imc.demo.auth.jwt.providers.TokenProvider;
import org.imc.demo.auth.jwt.providers.UserService;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired UserService userService;
    @Autowired TokenProvider tokenProvider;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Map<String, String> doLogin(@RequestParam(name = "username", required = true) String userName, @RequestParam(name = "password", required = true) String password) throws NotAuthorizedException, JoseException {
        Map<String, String> response = new HashMap<>();

        User user = userService.getUser(userName, password);
        if  (user == null) {
            throw new NotAuthorizedException("Invalid username/password");
        }
        response.put("token", tokenProvider.createToken(user));
        return response;
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void doLogout(@RequestParam(name = "token", required = true) String token) throws NotAuthorizedException {

        if (tokenProvider.isKnownToken(token)) {
            tokenProvider.invalidateToken(token);
        } else {
            throw new NotAuthorizedException("Invalid token");
        }

    }

    @ExceptionHandler(value = {NotAuthorizedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleNotAuthorized() {
        return "Not Authorized";
    }

}
