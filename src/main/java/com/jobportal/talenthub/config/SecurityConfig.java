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

    // Loads the user from the database using the login username/email.
    // Your CustomUserDetailsService ultimately calls UserRepository.findByEmail().
    private final CustomUserDetailsService customUserDetailsService;

    // BCrypt PasswordEncoder used to verify the password during authentication.
    // During registration it can also encode a plain password before saving it.
    private final PasswordEncoder passwordEncoder;

    // Custom filter that checks the JWT from each incoming request.
    // If the JWT is valid, it creates an authenticated SecurityContext.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor Injection:
    // Spring injects the required security dependencies into this class.
    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // CSRF protection is disabled because this application
                // uses stateless JWT authentication instead of session cookies.
                .csrf(csrf -> csrf.disable())


                // Configure how Spring Security manages HTTP sessions.
                .sessionManagement(session ->

                        // STATELESS means Spring Security does not maintain
                        // an authenticated server-side HTTP session.
                        // Each protected request must provide its JWT.
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // Define authentication and authorization rules.
                .authorizeHttpRequests(
                        auth -> auth


                                // Everything under /api/auth/** is public.
                                // Therefore registration and login do not require JWT.
                                .requestMatchers(
                                        "/api/auth/**"
                                )
                                .permitAll()


                                // ADMIN endpoints require ROLE_ADMIN.
                                // hasRole("ADMIN") internally checks for
                                // the authority ROLE_ADMIN.
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")


                                // ---------------- JOB RULES ----------------

                                // Any authenticated user can view jobs.
                                // Both JOB_SEEKER and RECRUITER can perform GET.
                                .requestMatchers(HttpMethod.GET, "/api/jobs/**")
                                .authenticated()


                                // Only RECRUITER can create jobs.
                                .requestMatchers(HttpMethod.POST, "/api/jobs/**")
                                .hasRole("RECRUITER")


                                // Only RECRUITER can completely update jobs.
                                .requestMatchers(HttpMethod.PUT, "/api/jobs/**")
                                .hasRole("RECRUITER")


                                // Only RECRUITER can partially update jobs.
                                .requestMatchers(HttpMethod.PATCH, "/api/jobs/**")
                                .hasRole("RECRUITER")


                                // Only RECRUITER can delete jobs.
                                .requestMatchers(HttpMethod.DELETE, "/api/jobs/**")
                                .hasRole("RECRUITER")


                                // -------- APPLICATION RULES --------

                                // Only RECRUITER can view applications
                                // through the recruiter-specific endpoint.
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/applications/recruiter"
                                )
                                .hasRole("RECRUITER")


                                // Only JOB_SEEKER can create an application.
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/applications/**"
                                )
                                .hasRole("JOB_SEEKER")


                                // Any authenticated user can access other
                                // application GET endpoints.
                                // The controller/service can apply additional
                                // business-level ownership checks.
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/applications/**"
                                )
                                .authenticated()


                                // Only RECRUITER can update application status.
                                // Example: APPLIED -> SHORTLISTED -> REJECTED.
                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/applications/*/status"
                                )
                                .hasRole("RECRUITER")


                                // Any authenticated user can reach the DELETE
                                // application endpoint at the security layer.
                                // Ownership/business rules should determine
                                // whether that particular user may delete it.
                                .requestMatchers(
                                        HttpMethod.DELETE,
                                        "/api/applications/**"
                                )
                                .authenticated()


                                // Catch-all rule:
                                // Any request not matched above requires authentication.
                                .anyRequest()
                                .authenticated()
                )


                // Tell Spring Security to use your custom AuthenticationProvider
                // for username/password authentication.
                .authenticationProvider(authenticationProvider())


                // Execute the JWT filter BEFORE Spring's standard
                // UsernamePasswordAuthenticationFilter.
                // This allows the JWT to authenticate the request before
                // authorization rules are evaluated.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        // Build and return the complete Spring Security filter chain.
        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

        // DaoAuthenticationProvider handles username/password authentication.
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();


        // Tell the provider to use your custom service to find the user.
        // Your service searches the users table by email.
        provider.setUserDetailsService(customUserDetailsService);


        // Tell the provider to use BCrypt to verify the supplied password
        // against the BCrypt hash stored in PostgreSQL.
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        // Spring Boot creates the AuthenticationManager.
        // AuthenticationConfiguration gives us access to that manager.
        return configuration.getAuthenticationManager();
    }
}