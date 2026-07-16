package com.dmpacademy.lesson.dto;

import java.time.Instant;
import java.util.UUID;

public record LessonResponse(
        UUID id,
        UUID moduleId,
        String title,
        String textContent,
        String videoUrl,
        String videoId,
        boolean isPremium,
        int orderIndex,
        Instant createdAt
) {}
