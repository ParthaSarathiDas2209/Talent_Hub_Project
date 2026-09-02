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

    // ==============================
    // AUTHENTICATION DEPENDENCIES
    // ==============================
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // ==============================
                // CSRF
                // ==============================
                .csrf(csrf -> csrf.disable())

                // ==============================
                // SESSION MANAGEMENT
                // ==============================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // ==============================
                // AUTHORIZATION
                // ==============================
                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // PUBLIC AUTHENTICATION ENDPOINTS
                        // ==============================
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // ==============================
                        // PUBLIC SWAGGER ENDPOINTS
                        // ==============================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // ==============================
                        // ADMIN ENDPOINTS
                        // ==============================
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")


                        // ==============================
                        // JOB SEEKER DASHBOARD
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/job-seeker"
                        )
                        .hasRole("JOB_SEEKER")

                        // ==============================
                        // RECRUITER DASHBOARD
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/recruiter"
                        )
                        .hasRole("RECRUITER")


                        // ==============================
                        // ADMIN DASHBOARD
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dashboard/admin"
                        )
                        .hasRole("ADMIN")

                        // ==============================
                        // JOB ENDPOINTS
                        // ==============================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/jobs/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/jobs/**"
                        )
                        .hasRole("RECRUITER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/jobs/**"
                        )
                        .hasRole("RECRUITER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/jobs/**"
                        )
                        .hasRole("RECRUITER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/jobs/**"
                        )
                        .hasRole("RECRUITER")


                        // ==============================
                        // APPLICATION ENDPOINTS
                        // ==============================

                        // Only JobSeekers can apply for jobs.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/applications"
                        )
                        .hasRole("JOB_SEEKER")

                        // Only recruiter can view applications.
                        // submitted to their jobs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/recruiter"
                        )
                        .hasRole("RECRUITER")

                        // Authenticated users can view applications.
                        // Ownership is checked in the service layer.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/applications/**"
                        )
                        .authenticated()

                        // Only recruiters can update application status.
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/applications/*/status"
                        )
                        .hasRole("RECRUITER")

                        // Authenticated users can delete applications.
                        // Ownership is checked in the service layer.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/applications/**"
                        )
                        .authenticated()

                        // ==============================
                        // NOTIFICATION ENDPOINTS
                        // ==============================

                        // Authenticated users can view their notifications.
                        // The logged-in user is identified from Authentication.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/notifications"
                        )
                        .authenticated()

                        // Authenticated users can mark their own notification as read.
                        // Ownership is checked using the logged-in user.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/notifications/*/read"
                        )
                        .authenticated()

                        // Authenticated users can check their unread notification count.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/notifications/unread/count"
                        )
                        .authenticated()

                        // ==============================
                        // USER ENDPOINTS
                        // ==============================

                        // Authenticated users can access user endpoints.
                        // Ownership and business rules are handled
                        // in the service layer.
                        .requestMatchers(
                                "/api/users/**"
                        )
                        .authenticated()


                        // ==============================
                        // DEFAULT RULE
                        // ==============================

                        // Any endpoint not explicitly configured above
                        // requires authentication.
                        .anyRequest()
                        .authenticated()
                )
//                        // ==============================
//                        // AUTHENTICATION PROVIDER
//                        // ==============================

//                        // Connects our custom AuthenticationProvider
//                        // to Spring Security's filter chain.
                .authenticationProvider(
                        authenticationProvider()
                )


                // ==============================
                // JWT AUTHENTICATION FILTER
                // ==============================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class

                );

        return http.build();
    }


    // ==============================
    // AUTHENTICATION PROVIDER
    // ==============================

    // Handles username/password authentication.
    //
    // Login
    //   ↓
    // AuthenticationProvider
    //   ↓
    // CustomUserDetailsService
    //   ↓
    // UserRepository
    //   ↓
    // PasswordEncoder
    //   ↓
    // Authentication result

    @Bean
    public AuthenticationProvider authenticationProvider() {

//        Depriciated/Old Method
//        DaoAuthenticationProvider provider =
//                new DaoAuthenticationProvider();
//
//        provider.setUserDetailsService(
//                customUserDetailsService
//        );

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        customUserDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}