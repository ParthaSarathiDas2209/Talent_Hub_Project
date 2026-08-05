package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO used to receive login credentials from the client.
// Only authentication-related input is accepted here.
public record LoginRequestDto(

        // Email is required and must follow a valid email format.
        @Email
        @NotBlank
        String email,

        // Password is required.
        // Password verification is handled by the authentication layer,
        // not by the DTO itself.
        @NotBlank
        String password

        // Future consideration:
        // No role field should be added here.
        //
        // The user's role must come from the database/UserDetails,
        // not from the login request.
        //
        // This prevents a client from sending:
        // "role": "ADMIN"
        // and attempting to authenticate as another role.
) {
}