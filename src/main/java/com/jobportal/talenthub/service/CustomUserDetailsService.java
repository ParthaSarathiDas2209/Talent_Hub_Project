package com.jobportal.talenthub.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService
        extends UserDetailsService {

    // CustomUserDetailsService inherits
    // loadUserByUsername() from Spring Security's
    // UserDetailsService interface.
    //
    // The actual implementation is provided by:
    //
    // CustomUserDetailsServiceImpl
    //
    // This interface exists mainly to give TalentHub
    // its own application-specific service abstraction.
}