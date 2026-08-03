package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.AuthResponseDto;
import com.jobportal.talenthub.dto.LoginRequestDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.entity.UserStatus;
import com.jobportal.talenthub.exception.InvalidCredentialsException;
import com.jobportal.talenthub.exception.JobApplicationException;
import com.jobportal.talenthub.mapper.UserMapper;
import com.jobportal.talenthub.repository.UserRepository;
import com.jobportal.talenthub.service.AuthService;
import com.jobportal.talenthub.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    // Handles authentication using email and password.
    private final AuthenticationManager authenticationManager;

    // Used to find and save users in the database.
    private final UserRepository userRepository;

    // Used to securely encode passwords using BCrypt.
    private final PasswordEncoder passwordEncoder;

    // Used to generate JWT after successful authentication.
    private final JwtService jwtService;


    // Constructor Injection:
    // Spring injects all required authentication dependencies.
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    @Override
    public UserResponseDto register(UserRequestDto userRequestDto) {

        // STEP 1:
        // Check whether the email is already registered.
        if (userRepository.existsByEmail(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email already exists"
            );
        }


        // STEP 2:
        // Convert registration DTO into User entity.
        User user = UserMapper.toEntity(userRequestDto);


        // STEP 3:
        // Never store the plain-text password.
        // Encode the password using BCrypt before saving.
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()
                )
        );


        // STEP 4:
        // New users start with ACTIVE status.
        user.setStatus(UserStatus.ACTIVE);


        // STEP 5:
        // New users are not deleted.
        user.setDeleted(false);
        user.setDeletedAt(null);


        // STEP 6:
        // Save the new user in the database.
        User savedUser = userRepository.save(user);


        // STEP 7:
        // Return a response DTO instead of exposing the entity.
        // The response DTO does not contain the password.
        return UserMapper.toResponseDto(savedUser);
    }


    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        try {

            // STEP 1:
            // Authenticate the user's email and password.
            //
            // AuthenticationManager
            //        ↓
            // DaoAuthenticationProvider
            //        ↓
            // CustomUserDetailsService
            //        ↓
            // Find user by email
            //        ↓
            // BCrypt password verification
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.email(),
                            loginRequestDto.password()
                    )
            );

        } catch (AuthenticationException exception) {

            // STEP 2:
            // Convert Spring Security authentication failure
            // into the application's custom exception.
            throw new InvalidCredentialsException(
                    "Invalid E-mail or Password."
            );
        }


        // STEP 3:
        // Find the authenticated user from the database.
        User user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid e-mail or password"
                        )
                );


        // STEP 4:
        // Prevent deleted users from logging in.
        if (user.isDeleted()) {

            throw new InvalidCredentialsException(
                    "User account is deleted."
            );
        }


        // STEP 5:
        // Only ACTIVE users are allowed to log in.
        //
        // INACTIVE, SUSPENDED and DEACTIVATED users
        // cannot receive a JWT.
        if (user.getStatus() != UserStatus.ACTIVE) {

            throw new InvalidCredentialsException(
                    "User account is not active."
            );
        }


        // STEP 6:
        // Generate JWT only after all authentication
        // and account-status checks have passed.
        String token = jwtService.generateToken(
                user.getEmail()
        );


        // STEP 7:
        // Return JWT along with basic user information.
        return new AuthResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}