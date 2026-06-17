package com.jobportal.talenthub.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationRequestDto(

        @NotNull(message = "User Id is required")
        Long userId,

        @NotNull(message = "Job Id is required")
        Long jobId
) {
}
