package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPatchDto(

        String firstName,

        String lastName,

        @Email
        String email,

        @Size(min = 6, max = 30)
        String password
) {
}