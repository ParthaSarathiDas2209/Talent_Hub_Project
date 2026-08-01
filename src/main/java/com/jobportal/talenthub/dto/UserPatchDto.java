package com.jobportal.talenthub.dto;

import com.jobportal.talenthub.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserPatchDto(

        String firstName,

        String lastName,

        @Email
        String email,

        @Size(min = 6, max = 30)
        String password,

        Role role
) {
}