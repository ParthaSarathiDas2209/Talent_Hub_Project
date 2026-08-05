package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.ApplicationStatus;

import java.time.LocalDateTime;

// DTO used to send application information back to the client.
// Keeps the response separate from the Application entity
// and avoids exposing complete User/Job entities.
public record ApplicationResponseDto(

        // Unique ID of the application.
        Long id,

        // ID of the user who submitted the application.
        // Returning only the ID avoids exposing the complete User entity.
        Long userId,

        // ID of the job for which the application was submitted.
        // Returning only the ID avoids exposing the complete Job entity.
        Long jobId,

        // Current status of the application.
        // Example: APPLIED, SHORTLISTED, INTERVIEWED,
        // ACCEPTED, REJECTED, WITHDRAWN.
        ApplicationStatus status,

        // Date and time when the application was submitted.
        LocalDateTime appliedAt

        // =========================================================
        // FUTURE CONSIDERATIONS
        // =========================================================

        // LocalDateTime updatedAt
        // → Can be added later if Application gets an updatedAt field.
        //   Useful for tracking when application status was last changed.
        //
        // String userName / userEmail
        // → Can be added later if the recruiter dashboard needs
        //   candidate information directly in the response.
        //
        // String jobTitle
        // → Can be added later if the candidate/recruiter dashboard
        //   needs the job title without making another API request.
        //
        // UserResponseDto user
        // JobResponseDto job
        // → Avoid returning complete nested entities unnecessarily.
        //   Prefer dedicated DTO fields or IDs depending on the API need.
) {
}
