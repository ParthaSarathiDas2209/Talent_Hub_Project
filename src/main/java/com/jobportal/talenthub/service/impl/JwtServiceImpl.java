package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service // Registers JwtServiceImpl as a Spring Bean.
public class JwtServiceImpl implements JwtService {

    // Secret key used to sign and verify JWT tokens.
    // Loaded from application.properties.
    @Value("${jwt.secret}")
    private String secret;

    // JWT expiration time (in milliseconds).
    // Loaded from application.properties.
    @Value("${jwt.expiration}")
    private long expiration;

    // Converts the secret String into a SecretKey object.
    // This key is used for both generating and verifying JWT signatures.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Parses the JWT, verifies its signature,
    // and returns all claims (payload) stored inside it.
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String generateToken(String email) {

        // Builds a new JWT containing the user's email,
        // issued time, expiration time, and digital signature.
        return Jwts.builder()

                // Stores the user's identity.
                .subject(email)

                // Stores the token creation time.
                .issuedAt(new Date())

                // Sets token expiry time.
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )

                // Digitally signs the JWT using the SecretKey.
                .signWith(getSigningKey())

                // Generates the final compact JWT String.
                .compact();
    }

    @Override
    public String extractEmail(String token) {

        // Returns the email (subject) stored inside the JWT.
        return extractAllClaims(token).getSubject();
    }

    @Override
    public Date extractExpiration(String token) {

        // Returns the token expiration date.
        return extractAllClaims(token).getExpiration();
    }

    @Override
    public boolean isTokenExpired(String token) {

        // Returns true if the current time
        // is after the token's expiration time.
        return extractExpiration(token).before(new Date());

    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {

        // Token is valid only if:
        // 1. Email inside JWT matches logged-in user.
        // 2. Token has not expired.
        return extractEmail(token)
                .equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

}