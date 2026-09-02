package com.jobportal.talenthub.dto;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        long id,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {
}