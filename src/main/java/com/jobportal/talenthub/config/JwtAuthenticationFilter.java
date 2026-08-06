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

    // Service responsible for JWT creation, extraction and validation.
    private final JwtService jwtService;

    // Loads the latest user information from the database using email.
    private final CustomUserDetailsService userDetailsService;


    // Constructor Injection:
    // Spring injects the JWT service and user-details service.
    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    // This method runs once for every HTTP request.
    //
    // Request
    //   ↓
    // JwtAuthenticationFilter
    //   ↓
    // Validate JWT
    //   ↓
    // Store Authentication in SecurityContext
    //   ↓
    // Continue to Controller
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        // STEP 1:
        // Read the Authorization header from the incoming request.
        //
        // Expected format:
        //
        // Authorization: Bearer <JWT>
        String authHeader =
                request.getHeader("Authorization");


//        System.out.println(
//                "Authorization header : " + authHeader
//        );


        // STEP 2:
        // If the Authorization header is missing
        // or does not contain a Bearer token,
        // skip JWT authentication.
        //
        // The request continues through the remaining filters.
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        // STEP 3:
        // Remove the "Bearer " prefix.
        //
        // Example:
        //
        // "Bearer eyJhbGciOi..."
        //          ↓
        // "eyJhbGciOi..."
        //
        // substring(7) removes:
        // B e a r e r + space
        String jwt = authHeader.substring(7);


        // STEP 4:
        // Extract the user's email from the JWT subject.
        //
        // JWT
        //   ↓
        // extractEmail()
        //   ↓
        // Email
        String email =
                jwtService.extractEmail(jwt);

//        System.out.println(
//                "Email extracted: " + email
//        );

        // STEP 5:
        // Continue authentication only when:
        //
        // 1. Email was successfully extracted.
        // 2. The request has not already been authenticated.
        if (email != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            // STEP 6:
            // Load the latest user information from the database.
            //
            // Email
            //   ↓
            // UserDetailsService
            //   ↓
            // UserRepository
            //   ↓
            // UserDetails
            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);


//            System.out.println(
//                    "Authorities : " +
//                            userDetails.getAuthorities()
//            );


            // STEP 7:
            // Validate the JWT against the current user.
            //
            // Validation checks include:
            //
            // JWT email
            //      VS
            // UserDetails username
            //
            // AND
            //
            // JWT expiration
            if (jwtService.isTokenValid(
                    jwt,
                    userDetails)) {


//                System.out.println("JWT valid");


                // STEP 8:
                // Create a Spring Security Authentication object.
                //
                // userDetails
                //   → authenticated principal
                //
                // null
                //   → no password/credentials are required here
                //
                // authorities
                //   → ROLE_JOB_SEEKER / ROLE_RECRUITER / ROLE_ADMIN
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                // STEP 9:
                // Store the authenticated user inside
                // Spring Security's SecurityContext.
                //
                // After this:
                //
                // SecurityContext
                //        ↓
                // Authenticated User
                //        ↓
                // Controller
                //
                // This is what allows Spring Security to know
                // who is making the request.
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);


//                System.out.println(
//                        "Stored Authorities : " +
//                                SecurityContextHolder
//                                        .getContext()
//                                        .getAuthentication()
//                                        .getAuthorities()
//                );

//                System.out.println(
//                        "Authentication Stored"
//                );
            }
        }


        // STEP 10:
        // Continue the filter chain.
        //
        // JWT authentication is now available through
        // SecurityContextHolder for the remaining security
        // filters and eventually the Controller.
        filterChain.doFilter(request, response);
    }
}