package com.dmpacademy.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CodeSubmissionRequest(
        @NotBlank(message = "Code is required")
        @Size(max = 50000, message = "Code must not exceed 50,000 characters")
        String code
) {}
