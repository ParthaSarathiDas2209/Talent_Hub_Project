package com.jobportal.talenthub.dto;

public record JobResponseDto(
        Long id,
        String title,
        String description,
        String companyName,
        String companyEmail,
        String companyPhone,
        String location,
        Long salary,
        Long recruiterId
) {
}
