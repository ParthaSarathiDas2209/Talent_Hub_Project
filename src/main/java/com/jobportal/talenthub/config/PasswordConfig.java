package com.jobportal.talenthub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    // Creates a Spring-managed PasswordEncoder bean.
    // BCrypt is used to securely hash passwords before storing them.
    @Bean
    public PasswordEncoder passwordEncoder() {

        // Returns BCryptPasswordEncoder implementation.
        // The same encoder is used later to verify passwords during login.
        return new BCryptPasswordEncoder();
    }
}
