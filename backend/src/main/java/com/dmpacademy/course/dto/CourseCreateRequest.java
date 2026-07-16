package com.dmpacademy.course.dto;

import com.dmpacademy.course.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CourseCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        String description,

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        Boolean isPremium
) {}
