package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JobRequestDto(

        @NotBlank(message = "Title is Required")
        String title,

        @NotBlank(message = "Description is Required")
        String description,

        @NotBlank(message = "Company Name is Required")
        String companyName,

        @Email(message = "Invalid company e-mail")
        @NotBlank(message = "Company e-mail is Required")
        String companyEmail,

        @NotBlank(message = "Company Phone is Required")
        String companyPhone,

        @NotBlank(message = "Location is Required")
        String location,

        @NotNull(message = "Salary is Required")
        Long salary,

        @NotNull(message = "Recruiter Id is Required")
        Long recruiterId
) {
}
