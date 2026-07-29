package com.jobportal.talenthub.dto;

import java.time.LocalDateTime;

public record JobResponseDto(
        Long id,
        String title,
        String description,
        String companyName,
        String companyEmail,
        String companyPhone,
        String location,
        Long salary,
        Long recruiterId,

        LocalDateTime applicationStartTime,
        LocalDateTime applicationEndTime

) {
}
