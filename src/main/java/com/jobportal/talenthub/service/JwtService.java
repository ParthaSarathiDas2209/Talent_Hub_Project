package com.jobportal.talenthub.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    // =========================================================
    // JWT GENERATION
    // =========================================================

    // Generate a signed JWT for the authenticated user's email.
    String generateToken(String email);


    // =========================================================
    // JWT CLAIM EXTRACTION
    // =========================================================

    // Extract the user's email from the JWT subject.
    String extractEmail(String token);

    // Extract the expiration timestamp from the JWT.
    Date extractExpiration(String token);


    // =========================================================
    // JWT VALIDATION
    // =========================================================

    // Check whether the JWT has expired.
    boolean isTokenExpired(String token);

    // Validate that:
    // 1. JWT email matches the authenticated user's username.
    // 2. JWT has not expired.
    boolean isTokenValid(
            String token,
            UserDetails userDetails
    );
}