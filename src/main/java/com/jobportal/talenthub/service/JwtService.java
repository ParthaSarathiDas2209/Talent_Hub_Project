package com.jobportal.talenthub.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateToken(String email);

    String extractEmail(String token);

    Date extractExpiration(String token);

    boolean isTokenExpired(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

}
