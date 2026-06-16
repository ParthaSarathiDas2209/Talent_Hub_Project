package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;

public record JobPatchDto(

        String title,

        String description,

        String companyName,

        @Email(message = "Invalid company e-mail")
        String companyEmail,

        String companyPhone,

        String location,

        @Positive
        Long salary

) {
}
