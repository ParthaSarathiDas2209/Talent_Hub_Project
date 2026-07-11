package com.jobportal.talenthub.config;

import com.jobportal.talenthub.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    // Custom service that loads user details from database
    private final CustomUserDetailsService customUserDetailsService;

    // Used to encode passwords during registration
    // and verify passwords during login
    private final PasswordEncoder passwordEncoder;

    // Custom JWT filter that validates JWT on every request
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor Injection
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

                // Disable CSRF because JWT is Stateless.
                // CSRF is mainly used with Session + Cookies.
                .csrf(csrf -> csrf.disable())

                // Configure Session Management
                .sessionManagement(session ->

                        // Never create HTTP Session.
                        // Every request must contain JWT.
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Configure Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // Authentication APIs are public.
                        // Register/Login doesn't require JWT.
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // Every other API requires authentication.
                        .anyRequest()
                        .authenticated()
                )

                // Tell Spring which AuthenticationProvider
                // should verify username & password.
                .authenticationProvider(authenticationProvider())

                // Run JWT Filter before Spring's
                // UsernamePasswordAuthenticationFilter.
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // Build complete Spring Security Filter Chain
        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {

        // DaoAuthenticationProvider handles
        // username/password authentication.
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        // Load user from database.
        provider.setUserDetailsService(customUserDetailsService);

        // Verify password using BCrypt.
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        // Spring already creates AuthenticationManager.
        // We simply obtain it from AuthenticationConfiguration.
        return configuration.getAuthenticationManager();
    }
}