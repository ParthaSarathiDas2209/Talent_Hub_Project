package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// DTO used for partial updates to an existing user.
// Unlike UserRequestDto, fields are optional because a PATCH
// request may update only one or a few fields.
public record UserPatchDto(

        // Optional first-name update.
        // If not provided, the existing first name remains unchanged.
        String firstName,

        // Optional last-name update.
        // If not provided, the existing last name remains unchanged.
        String lastName,

        // Optional email update.
        // If provided, it must follow a valid email format.
        @Email
        String email,

        // Optional password update.
        // If provided, it must contain between 6 and 30 characters.
        // The password must still be encoded with PasswordEncoder
        // before being stored in the database.
        @Size(min = 6, max = 30)
        String password,

        // Optional role update.
        // The service layer should control who is allowed
        // to change a user's role.
        Role role
) {
}