package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;

// DTO returned after successful login/authentication.
// Contains the JWT and basic information about the authenticated user.
public record AuthResponseDto(

        // JWT used by the client to authenticate future protected requests.
        String token,

        // ID of the authenticated user.
        Long userId,

        // Email/username associated with the authenticated account.
        String email,

        // Role of the authenticated user.
        // Used by the client to understand the user's role,
        // while actual authorization is still enforced by Spring Security.
        Role role

        // Future consideration:
        // Refresh token
        // → Can be added later if TalentHub implements refresh-token authentication.
        //
        // Token expiration information
        // → Can be added later if the frontend needs to know
        //   when the access token expires.
) {
}