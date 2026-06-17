package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponseDto(

        Long id,
        Long userId,
        Long jobId,
        ApplicationStatus status,
        LocalDateTime appliedAt
) {
}
