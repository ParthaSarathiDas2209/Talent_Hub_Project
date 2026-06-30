package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service // Marks this class as a Spring Bean so it can be injected where needed.
public class JwtServiceImpl implements JwtService {

    // Reads the secret key from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Reads the token expiration time (in milliseconds) from application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    @Override
    public String generateToken(String email) {

        // Converts the secret String into a SecretKey object.
        // JWT uses this SecretKey to digitally sign the token.
        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        // Starts building the JWT
        return Jwts.builder()

                // Stores the identity (subject) of the token.
                // Here we are storing the user's email.
                .subject(email)

                // Stores the current date & time when the token was created.
                .issuedAt(new Date())

                // Sets the expiry time of the token.
                // Current Time + expiration (e.g., 24 hours)
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )

                // Digitally signs the token using our SecretKey.
                // This prevents anyone from modifying the token.
                .signWith(key)

                // Converts the JWT Builder into the final compact JWT String.
                .compact();
    }

    @Override
    public String extractEmail(String token) {

        // Create the same SecretKey that was used while generating the JWT.
        // This key is required to verify the JWT's digital signature.
        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts

                // Creates a JWT parser for reading JWT tokens.
                .parser()

                // Verifies that the JWT signature matches our SecretKey.
                // If someone modified the token, verification fails here.
                .verifyWith(key)

                // Builds the parser.
                .build()

                // Parses the signed JWT and extracts its Claims.
                .parseSignedClaims(token)

                // Retrieves the Payload (Claims) from the JWT.
                .getPayload()

                // Returns the Subject (sub), which is the user's email.
                .getSubject();
    }

    @Override
    public Date extractExpiration(String token) {
        SecretKey key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    @Override
    public boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());

    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractEmail(token)
                .equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }


}