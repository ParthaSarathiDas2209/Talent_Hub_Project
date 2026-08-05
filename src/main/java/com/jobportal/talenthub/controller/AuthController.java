package com.jobportal.talenthub.controller;

import com.jobportal.talenthub.dto.AuthResponseDto;
import com.jobportal.talenthub.dto.LoginRequestDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;
import com.jobportal.talenthub.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Service layer responsible for registration and login business logic.
    private final AuthService authService;


    // Constructor Injection:
    // Spring injects the AuthService dependency.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // ==========================================================
    // USER REGISTRATION
    // ==========================================================

    // POST /api/auth/register
    //
    // Purpose:
    // Create a new user account.
    //
    // Flow:
    //
    // Client
    //   ↓
    // UserRequestDto
    //   ↓
    // Validation using @Valid
    //   ↓
    // AuthService
    //   ↓
    // Password Encoding
    //   ↓
    // Save User
    //   ↓
    // UserResponseDto
    //
    // 201 CREATED is returned when registration succeeds.
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(
            @Valid
            @RequestBody UserRequestDto userRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        authService.register(userRequestDto)
                );
    }


    // ==========================================================
    // USER LOGIN
    // ==========================================================

    // POST /api/auth/login
    //
    // Purpose:
    // Authenticate an existing user and generate a JWT.
    //
    // Flow:
    //
    // Client
    //   ↓
    // LoginRequestDto
    //   ↓
    // Validation
    //   ↓
    // AuthService
    //   ↓
    // AuthenticationManager
    //   ↓
    // Password Verification
    //   ↓
    // Generate JWT
    //   ↓
    // AuthResponseDto
    //
    // 200 OK is returned when login succeeds.
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid
            @RequestBody LoginRequestDto loginRequestDto) {

        return ResponseEntity.ok(
                authService.login(loginRequestDto)
        );
    }
}