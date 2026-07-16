package com.dmpacademy.lesson.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LessonCreateRequest(
        @NotBlank(message = "Lesson title is required")
        @Size(max = 200, message = "Lesson title must not exceed 200 characters")
        String title,

        @Size(max = 50000, message = "Text content must not exceed 50,000 characters")
        String textContent,

        String videoUrl,

        Boolean isPremium
) {}
