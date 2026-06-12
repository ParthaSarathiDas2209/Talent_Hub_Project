package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;

import java.time.LocalDateTime;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        LocalDateTime createdAt
) {
}
