package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

// DTO used to receive job creation data from the client.
// Validation ensures required fields are present before
// the request reaches the service layer.
public record JobRequestDto(

        // Job title is required.
        @NotBlank(message = "Title is Required")
        String title,

        // Job description is required.
        @NotBlank(message = "Description is Required")
        String description,

        // Company name is required.
        @NotBlank(message = "Company Name is Required")
        String companyName,

        // Company email is required and must have a valid email format.
        @Email(message = "Invalid company e-mail")
        @NotBlank(message = "Company e-mail is Required")
        String companyEmail,

        // Company phone number is required.
        @NotBlank(message = "Company Phone is Required")
        String companyPhone,

        // Job location is required.
        @NotBlank(message = "Location is Required")
        String location,

        // Salary is required.
        @NotNull(message = "Salary is Required")
        Long salary,

        // Defines when candidates can start applying.
        @NotNull(message = "Application Start Time is Required")
        LocalDateTime applicationStartTime,

        // Defines when the application window closes.
        @NotNull(message = "Application End Time is Required")
        LocalDateTime applicationEndTime

        // =========================================================
        // RECRUITER OWNERSHIP
        // =========================================================

        // recruiterId is intentionally not accepted from the client.
        //
        // The authenticated JWT identifies the recruiter.
        // The service layer obtains the authenticated user and
        // assigns that user as the Job.recruiter.
        //
        // This prevents a recruiter from supplying another user's ID.
        //
        // @NotNull(message = "Recruiter Id is Required")
        // Long recruiterId

        // =========================================================
        // FUTURE VALIDATION IMPROVEMENTS
        // =========================================================

        // 1. Salary validation
        // → Salary should be greater than 0.
        //
        // Future example:
        // @Positive(message = "Salary must be greater than 0")
        // Long salary
        //
        // 2. Application date validation
        // → applicationStartTime must be before applicationEndTime.
        //
        // This is better handled as business validation because
        // the relationship is between two fields.
        //
        // Example:
        // applicationStartTime < applicationEndTime
        //
        // 3. Phone validation
        // → Add a phone-number format/pattern if TalentHub
        //   requires a specific phone format.
        //
        // Future example:
        // @Pattern(...)
        // String companyPhone
        //
        // 4. Title length
        // → Add minimum/maximum length if required.
        //
        // Future example:
        // @Size(min = ..., max = ...)
        // String title
        //
        // 5. Description length
        // → Add reasonable minimum/maximum limits if required.
        //
        // Future example:
        // @Size(min = ..., max = ...)
        // String description
) {
}
