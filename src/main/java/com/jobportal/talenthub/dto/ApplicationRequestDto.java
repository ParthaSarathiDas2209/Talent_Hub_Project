package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.NotNull;

// DTO used when a JOB_SEEKER submits an application for a job.
// The client only needs to provide the job they want to apply for.
public record ApplicationRequestDto(

        // User ID is intentionally not accepted from the client.
        //
        // The authenticated user's identity is obtained from
        // the JWT/SecurityContext in the service layer.
        //
        // This prevents a user from sending another user's ID
        // and applying on their behalf.
        //
        // @NotNull(message = "User Id is required")
        // Long userId,

        // ID of the job for which the user wants to apply.
        // This is required to identify the target job.
        @NotNull(message = "Job Id is required")
        Long jobId

        // Future validation/business rules:
        //
        // 1. Job must exist.
        //
        // 2. Job must not be soft-deleted.
        //
        // 3. Job must be ACTIVE.
        //
        // 4. Current time must be inside the application window:
        //
        //    applicationStartTime <= now <= applicationEndTime
        //
        // 5. User must not have already applied to the same job.
        //
        // 6. Only JOB_SEEKER should be allowed to create applications.
        //
        // These are business rules and belong primarily
        // in ApplicationService, not inside this DTO.
) {
}