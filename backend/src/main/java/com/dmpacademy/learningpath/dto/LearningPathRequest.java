package com.dmpacademy.learningpath.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record LearningPathRequest(
        @NotBlank @Size(max = 150) String name,
        String description,
        @NotEmpty @Size(max = 50) List<UUID> courseIds
) {}
