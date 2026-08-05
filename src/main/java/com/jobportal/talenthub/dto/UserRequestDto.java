package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO used to receive user data when creating/registering a user.
// Validation annotations ensure invalid input is rejected before reaching the service layer.
public record UserRequestDto(

        // First name cannot be null, empty, or only whitespace.
        @NotBlank(message = "First Name is Required")
        String firstName,

        // Last name cannot be null, empty, or only whitespace.
        @NotBlank(message = "Last Name is Required")
        String lastName,

        // Ensures the value follows a valid email format.
        // @NotBlank ensures an email must actually be provided.
        @Email
        @NotBlank
        String email,

        // Password is required and must contain between 6 and 30 characters.
        // The actual password should be encoded using PasswordEncoder
        // before it is stored in the database.
        @NotBlank
        @Size(min = 6, max = 30)
        String password,

        // Role is required so the application knows the user's authorization level.
        @NotNull
        Role role
) {
}
