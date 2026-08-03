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
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username : " + username)
                );

        if (user.isDeleted()) {
            throw new UsernameNotFoundException(
                    "User account is deleted."
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UsernameNotFoundException(
                    "User account is not active."
            );
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name()
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(authority)
        );
    }
}