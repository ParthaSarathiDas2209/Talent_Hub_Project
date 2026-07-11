package com.jobportal.talenthub.config;

import com.jobportal.talenthub.service.CustomUserDetailsService;
import com.jobportal.talenthub.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Read Authorization header from incoming request
        String authHeader = request.getHeader("Authorization");

        // If Authorization header is missing OR
        // it is not a Bearer token, skip JWT authentication
        // and continue with the next filter.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " and keep only the JWT token
        String jwt = authHeader.substring(7);

        // Extract email from JWT
        String email = jwtService.extractEmail(jwt);

        // Authenticate only if email exists
        // and Spring Security has not already authenticated this request
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load latest user details from database
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);

            // Validate JWT against database user
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Create Spring Security Authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Store authenticated user in Spring Security Context
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }

        // Continue remaining filters and eventually reach Controller
        filterChain.doFilter(request, response);
    }
}

