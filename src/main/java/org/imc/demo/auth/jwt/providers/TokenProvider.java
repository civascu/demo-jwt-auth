package org.imc.demo.auth.jwt.providers;

import org.imc.demo.auth.jwt.exceptions.NotAuthorizedException;
import org.imc.demo.auth.jwt.models.User;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TokenProvider {
    public static final String ISSUER = "auth.demo.com";
    public static final String AUDIENCE = "ui.demo.com";
    private List<String> tokens;
    private RsaJsonWebKey rsaJsonWebKey;

    @Autowired
    UserService userService;

    public TokenProvider() throws JoseException {
        tokens = new ArrayList<>();
        rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
    }

    public boolean isKnownToken(String token) {
        return tokens.contains(token);
    }

    public String recordToken(String token) {
        if (!tokens.contains(token)) {
            tokens.add(token);
        }
        return token;
    }

    public boolean invalidateToken(String token) {
        if (tokens.contains(token)) {
            return tokens.remove(token);
        }

        return false;
    }

    public String createToken(User user) throws JoseException {

        JwtClaims claims = new JwtClaims();
        claims.setIssuer(ISSUER);  // who creates the token and signs it
        claims.setAudience(AUDIENCE); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(user.getId()); // the subject/principal is whom the token is about
        claims.setClaim("email", user.getEmail()); // additional claims/attributes about the subject can be added

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        String token = jws.getCompactSerialization();
        tokens.add(token);

        return token;
    }

    public Map<String, Object> unpackToken(String token) throws NotAuthorizedException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer(ISSUER) // whom the JWT needs to have been issued by
                .setExpectedAudience(AUDIENCE) // to whom the JWT is intended for
                .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
                .build(); // create the JwtConsumer instance

        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            User user = userService.getById(jwtClaims.getSubject());

            if (user == null) {
                throw new NotAuthorizedException("Invalid user id");
            }

            Map<String, Object> ret = jwtClaims.getClaimsMap();
            ret.put("valid", true);
            return ret;

        }
        catch (InvalidJwtException e)
        {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            throw new NotAuthorizedException("Invalid JWT");
        } catch (MalformedClaimException e) {
            throw new NotAuthorizedException("Malformed claim");
        }
    }
}
