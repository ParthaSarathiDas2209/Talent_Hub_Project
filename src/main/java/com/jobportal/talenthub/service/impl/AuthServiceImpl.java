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

    // AuthenticationManager is responsible for verifying
    // the supplied email and password through Spring Security.
    private final AuthenticationManager authenticationManager;

    // Repository used to check existing users during registration
    // and retrieve users during login.
    private final UserRepository userRepository;

    // BCrypt PasswordEncoder used to securely hash passwords.
    private final PasswordEncoder passwordEncoder;

    // JwtService generates the JWT after successful authentication.
    private final JwtService jwtService;


    // Constructor Injection:
    // Spring automatically provides all required dependencies.
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


    // =========================================================
    // REGISTER USER
    // =========================================================
    //
    // Flow:
    //
    // UserRequestDto
    //       ↓
    // Check email
    //       ↓
    // Convert DTO → Entity
    //       ↓
    // Encode password
    //       ↓
    // Set account defaults
    //       ↓
    // Save User
    //       ↓
    // Return UserResponseDto

    @Override
    public UserResponseDto register(UserRequestDto userRequestDto) {

        // Prevent registration with an email that already exists.
        if (userRepository.existsByEmail(userRequestDto.email())) {

            throw new JobApplicationException(
                    "Email already exists"
            );
        }


        // Convert the incoming registration DTO
        // into a User entity.
        User user = UserMapper.toEntity(userRequestDto);


        // Never store a plain-text password.
        // BCrypt hashes the password before it reaches the database.
        user.setPassword(
                passwordEncoder.encode(
                        userRequestDto.password()
                )
        );


        // Newly registered users start as ACTIVE.
        user.setStatus(UserStatus.ACTIVE);


        // Newly registered users are not soft-deleted.
        user.setDeleted(false);
        user.setDeletedAt(null);


        // Persist the new user.
        User savedUser = userRepository.save(user);


        // Return a DTO instead of exposing the User entity.
        // The response DTO intentionally does not contain
        // the user's password.
        return UserMapper.toResponseDto(savedUser);
    }


    // =========================================================
    // LOGIN USER
    // =========================================================
    //
    // Flow:
    //
    // LoginRequestDto
    //       ↓
    // AuthenticationManager
    //       ↓
    // UserDetailsService
    //       ↓
    // Load user
    //       ↓
    // Verify BCrypt password
    //       ↓
    // Check account status
    //       ↓
    // Generate JWT
    //       ↓
    // Return AuthResponseDto

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        try {

            // Ask Spring Security to authenticate
            // the supplied email and password.
            //
            // The UsernamePasswordAuthenticationToken
            // carries the credentials into Spring Security.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.email(),
                            loginRequestDto.password()
                    )
            );

        } catch (AuthenticationException exception) {

            // Hide the exact authentication failure reason
            // from the client.
            //
            // For example, do not reveal whether the email
            // or password was incorrect.
            throw new InvalidCredentialsException(
                    "Invalid E-mail or Password."
            );
        }


        // Authentication succeeded.
        // Retrieve the actual User entity from the database
        // so we can obtain its ID, role and account status.
        User user = userRepository
                .findByEmail(loginRequestDto.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid e-mail or password"
                        )
                );


        // A soft-deleted account must not be allowed to log in.
        if (user.isDeleted()) {

            throw new InvalidCredentialsException(
                    "User account is deleted."
            );
        }


        // Only ACTIVE accounts are allowed to receive a JWT.
        //
        // INACTIVE
        // SUSPENDED
        // DEACTIVATED
        //
        // cannot successfully log in.
        if (user.getStatus() != UserStatus.ACTIVE) {

            throw new InvalidCredentialsException(
                    "User account is not active."
            );
        }


        // Generate the JWT only after:
        //
        // 1. Credentials are valid
        // 2. User exists
        // 3. User is not deleted
        // 4. User account is ACTIVE
        String token = jwtService.generateToken(
                user.getEmail()
        );


        // Return the JWT and safe user information.
        //
        // Password is intentionally NOT included.
        return new AuthResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }
}