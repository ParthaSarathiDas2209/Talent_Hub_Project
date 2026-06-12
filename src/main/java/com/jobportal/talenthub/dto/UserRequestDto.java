package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDto(

        @NotBlank(message = "First Name is Required")
        String firstName,

        @NotBlank(message = "Last Name is Required")
        String lastName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, max = 30)
        String password,

        @NotNull
        Role role
) {
}
