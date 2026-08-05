package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.entity.UserStatus;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.CustomUserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsServiceImpl
        implements CustomUserDetailsService {

    // Repository used to find the application User
    // from the database using the email/username.
    private final UserRepository userRepository;


    // Constructor Injection:
    // Spring provides the UserRepository dependency.
    public CustomUserDetailsServiceImpl(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }


    // =========================================================
    // LOAD USER FOR SPRING SECURITY
    // =========================================================
    //
    // Spring Security calls this method when it needs
    // to authenticate a user.
    //
    // In TalentHub:
    //
    // username
    //    ↓
    // email
    //    ↓
    // UserRepository
    //    ↓
    // User entity
    //    ↓
    // Validate account
    //    ↓
    // Create Spring Security UserDetails
    //
    // The parameter is called "username" because that is the
    // method name required by Spring Security, but TalentHub
    // uses the user's email as the username.

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        // Find the application user using the supplied email.
        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with username : "
                                        + username
                        )
                );


        // Deleted users must not be authenticated.
        //
        // Soft delete means the database record remains,
        // but the account is no longer allowed to use the system.
        if (user.isDeleted()) {

            throw new UsernameNotFoundException(
                    "User account is deleted."
            );
        }


        // Only ACTIVE users are allowed to authenticate.
        //
        // INACTIVE
        // SUSPENDED
        // DEACTIVATED
        //
        // accounts are rejected here.
        if (user.getStatus() != UserStatus.ACTIVE) {

            throw new UsernameNotFoundException(
                    "User account is not active."
            );
        }


        // Convert the application's Role enum into
        // Spring Security's GrantedAuthority format.
        //
        // Example:
        //
        // Role.RECRUITER
        //       ↓
        // "ROLE_RECRUITER"
        //
        // Role.JOB_SEEKER
        //       ↓
        // "ROLE_JOB_SEEKER"
        //
        // Spring Security uses this authority for
        // role-based authorization.
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                );


        // Convert the application's User entity into
        // Spring Security's UserDetails object.
        //
        // Spring Security needs:
        //
        // username/email
        // password
        // authorities/roles
        //
        // The password here is already BCrypt-hashed
        // because it was encoded during registration.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
    }
}