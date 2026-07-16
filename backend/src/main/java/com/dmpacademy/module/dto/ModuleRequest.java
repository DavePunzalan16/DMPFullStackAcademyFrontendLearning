package com.dmpacademy.module.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModuleRequest(
        @NotBlank(message = "Module title is required")
        @Size(min = 1, max = 150, message = "Module title must be between 1 and 150 characters")
        String title
) {}
