package com.jobportal.talenthub.config;

import com.jobportal.talenthub.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Loads the user from the database during authentication.
    // CustomUserDetailsService ultimately searches the user by email.
    private final CustomUserDetailsService customUserDetailsService;

    // BCrypt encoder used to verify passwords during authentication.
    // It can also be used to encode passwords before storing them.
    private final PasswordEncoder passwordEncoder;

    // Custom filter responsible for extracting and validating
    // the JWT from incoming requests.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // Constructor Injection:
    // Spring injects all required security dependencies.
    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // Defines the main Spring Security filter chain.
    // This method controls authentication, authorization,
    // session management, CSRF and JWT filter placement.
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // Disable CSRF because the application uses
                // stateless JWT authentication instead of
                // traditional session-cookie authentication.
                .csrf(csrf -> csrf.disable())


                // Configure HTTP session management.
                .sessionManagement(session ->

                        // STATELESS means Spring Security does not
                        // maintain a server-side login session.
                        //
                        // Each protected request must carry
                        // a valid JWT.
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // Configure authorization rules for endpoints.
                .authorizeHttpRequests(
                        auth -> auth


                                // Authentication endpoints are public.
                                //
                                // Register:
                                // POST /api/auth/register
                                //
                                // Login:
                                // POST /api/auth/login
                                //
                                // These endpoints cannot require JWT
                                // because the user does not have one yet.
                                .requestMatchers(
                                        "/api/auth/**"
                                )
                                .permitAll()


                                // All admin endpoints require
                                // the ADMIN role.
                                //
                                // hasRole("ADMIN") internally checks
                                // for the authority ROLE_ADMIN.
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")


                                // ==============================
                                // JOB ENDPOINTS
                                // ==============================

                                // Any authenticated user can view jobs.
                                //
                                // JOB_SEEKER and RECRUITER can perform GET.
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/jobs/**"
                                )
                                .authenticated()


                                // Only recruiters can create jobs.
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/jobs/**"
                                )
                                .hasRole("RECRUITER")


                                // Only recruiters can completely
                                // update existing jobs.
                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/jobs/**"
                                )
                                .hasRole("RECRUITER")


                                // Only recruiters can partially
                                // update existing jobs.
                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/jobs/**"
                                )
                                .hasRole("RECRUITER")


                                // Only recruiters can delete jobs.
                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/jobs/**"
                                )
                                .hasRole("RECRUITER")


                                // ==============================
                                // APPLICATION ENDPOINTS
                                // ==============================

                                // Only recruiters can access the
                                // recruiter-specific application list.
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/applications/recruiter"
                                )
                                .hasRole("RECRUITER")


                                // Only job seekers can create applications.
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/applications/**"
                                )
                                .hasRole("JOB_SEEKER")


                                // Any authenticated user can reach
                                // application GET endpoints.
                                //
                                // Service-layer ownership checks decide
                                // whether the user can actually view
                                // the requested application.
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/applications/**"
                                )
                                .authenticated()


                                // Only recruiters can update application status.
                                //
                                // Example:
                                // APPLIED → SHORTLISTED
                                // SHORTLISTED → INTERVIEWED
                                // INTERVIEWED → ACCEPTED
                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/applications/*/status"
                                )
                                .hasRole("RECRUITER")


                                // Any authenticated user can reach
                                // the DELETE application endpoint.
                                //
                                // The service layer performs the ownership
                                // check to determine whether the user
                                // can actually delete that application.
                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/applications/**"
                                )
                                .authenticated()


                                // Catch-all rule.
                                //
                                // Any endpoint not explicitly configured
                                // above requires authentication.
                                .anyRequest()
                                .authenticated()
                )


                // Register the custom AuthenticationProvider.
                //
                // This provider connects Spring Security's
                // username/password authentication with
                // CustomUserDetailsService and PasswordEncoder.
                .authenticationProvider(
                        authenticationProvider()
                )


                // Add the JWT filter before Spring Security's
                // UsernamePasswordAuthenticationFilter.
                //
                // Flow:
                //
                // Request
                //    ↓
                // JwtAuthenticationFilter
                //    ↓
                // Validate JWT
                //    ↓
                // Set Authentication
                //    ↓
                // Authorization rules
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        // Build the configured SecurityFilterChain.
        return http.build();
    }


    // Creates the AuthenticationProvider used for
    // username/password authentication.
    @Bean
    public AuthenticationProvider authenticationProvider() {

        // DaoAuthenticationProvider performs authentication
        // using a UserDetailsService and PasswordEncoder.
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();


        // Tell the provider how to find users.
        //
        // CustomUserDetailsService
        //        ↓
        // UserRepository
        //        ↓
        // findByEmail()
        provider.setUserDetailsService(
                customUserDetailsService
        );


        // Tell the provider how to verify passwords.
        //
        // Plain password
        //       ↓
        // BCrypt comparison
        //       ↓
        // Stored BCrypt hash
        provider.setPasswordEncoder(
                passwordEncoder
        );


        return provider;
    }


    // Exposes AuthenticationManager as a Spring Bean.
    //
    // AuthServiceImpl uses this during login:
    //
    // AuthenticationManager
    //        ↓
    // AuthenticationProvider
    //        ↓
    // CustomUserDetailsService
    //        ↓
    // PasswordEncoder
    //        ↓
    // Authentication result
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        // Obtain the AuthenticationManager configured
        // by Spring Security.
        return configuration.getAuthenticationManager();
    }
}