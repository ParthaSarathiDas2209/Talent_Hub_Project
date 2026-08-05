package com.jobportal.talenthub.dto;

import java.time.LocalDateTime;

// DTO used to send job information back to the client.
// It exposes the required job details without returning
// the complete Job entity or User entity.
public record JobResponseDto(

        // Unique identifier of the job.
        Long id,

        // Job title.
        String title,

        // Detailed description of the job.
        String description,

        // Name of the company offering the job.
        String companyName,

        // Company contact email.
        String companyEmail,

        // Company contact phone number.
        String companyPhone,

        // Job location.
        String location,

        // Salary offered for the job.
        Long salary,

        // ID of the recruiter who created/owns the job.
        // We return the ID instead of exposing the complete User entity.
        Long recruiterId,

        // When applications can start being submitted.
        LocalDateTime applicationStartTime,

        // When applications stop being accepted.
        LocalDateTime applicationEndTime

        // =========================================================
        // Future consideration:
        // =========================================================

        // LocalDateTime createdAt
        // → Can be added if the API needs to show when the job was posted.

        // LocalDateTime updatedAt
        // → Can be added later if the API needs to show
        //   when the job was last modified.

        // JobStatus status
        // → Can be added later if the frontend needs to display
        //   whether the job is DRAFT, ACTIVE, CLOSED, etc.

        // boolean deleted 
        // → Normally should NOT be exposed to regular users. 
        // It is an internal soft-delete flag.

        // LocalDateTime deletedAt
        // → Normally should NOT be exposed to regular users.
        // It is an internal audit/deletion timestamp.

        // User recruiter
        // → Do NOT expose the complete recruiter entity unnecessarily.
        // recruiterId is sufficient for the current design.

        // If a future API needs recruiter information, create a
        // dedicated RecruiterResponseDto instead of exposing User directly.
) {
}
