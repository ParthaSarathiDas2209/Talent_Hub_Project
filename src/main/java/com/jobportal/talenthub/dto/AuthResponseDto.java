package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;

public record AuthResponseDto(
        String token,
        Long userId,
        String email,
        Role role
) {
}
