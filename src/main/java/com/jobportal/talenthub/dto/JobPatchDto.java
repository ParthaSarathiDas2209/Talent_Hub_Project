package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

// DTO used for partially updating an existing Job.
// Unlike JobRequestDto, fields are optional because PATCH
// should allow the client to update only selected fields.
public record JobPatchDto(

        // Optional job title update.
        String title,

        // Optional job description update.
        String description,

        // Optional company name update.
        String companyName,

        // If provided, company email must have a valid email format.
        @Email(message = "Invalid company e-mail")
        String companyEmail,

        // Optional company phone update.
        String companyPhone,

        // Optional location update.
        String location,

        // If salary is provided, it must be greater than zero.
        @Positive
        Long salary,

        // Optional application start-time update.
        LocalDateTime applicationStartTime,

        // Optional application end-time update.
        LocalDateTime applicationEndTime

        // =========================================================
        // FUTURE VALIDATION IMPROVEMENTS
        // =========================================================

        // 1. Title length
        // → Add @Size(min = ..., max = ...) if required.
        //
        // 2. Description length
        // → Add @Size(min = ..., max = ...) if required.
        //
        // 3. Company phone validation
        // → Add @Pattern(...) if TalentHub requires
        //   a specific phone-number format.
        //
        // 4. Application window validation
        // → Ensure applicationStartTime is before
        //   applicationEndTime.
        //
        // This is cross-field/business validation and is
        // better handled in the service layer.
        //
        // 5. PATCH empty-value handling
        // → Service layer should decide how null fields are handled.
        //   Usually:
        //
        //   null field → keep existing value
        //   supplied field → update existing value
        //
        // 6. Ownership validation
        // → The recruiter must only be allowed to PATCH
        //   their own job.
        //
        // This should NOT be trusted from the DTO.
        // The authenticated user/JWT should determine ownership.
) {
}