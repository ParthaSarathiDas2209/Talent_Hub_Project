package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;

import java.time.LocalDateTime;

// DTO used to send user information back to the client.
// Keeps the response separate from the User entity
// and prevents sensitive fields from being exposed.
public record UserResponseDto(

        // Unique identifier of the user.
        Long id,

        // User's first name.
        String firstName,

        // User's last name.
        String lastName,

        // User's email address.
        String email,

        // User's role in the TalentHub system.
        Role role,

        // Date and time when the user account was created.
        LocalDateTime createdAt

        // Future consideration:
        // LocalDateTime updatedAt
        // → Can be added later if the API needs to show
        //   when the user's information was last modified.
        //
        // UserStatus status
        // → Can be added later if the API needs to show
        //   whether the account is ACTIVE, INACTIVE,
        //   SUSPENDED, or DEACTIVATED.
) {
}
