package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.AuthResponseDto;
import com.jobportal.talenthub.dto.LoginRequestDto;
import com.jobportal.talenthub.dto.UserRequestDto;
import com.jobportal.talenthub.dto.UserResponseDto;

public interface AuthService {

    // =========================================================
    // REGISTRATION
    // =========================================================

    // Register a new user account.
    // Handles validation, password encoding and user creation
    // through AuthServiceImpl.
    UserResponseDto register(
            UserRequestDto userRequestDto
    );


    // =========================================================
    // LOGIN
    // =========================================================

    // Authenticate the user and return a JWT along with
    // basic authenticated-user information.
    AuthResponseDto login(
            LoginRequestDto loginRequestDto
    );
}