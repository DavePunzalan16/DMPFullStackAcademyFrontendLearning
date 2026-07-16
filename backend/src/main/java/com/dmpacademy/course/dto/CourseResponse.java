package com.dmpacademy.course.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        String description,
        UUID categoryId,
        String categoryName,
        String difficulty,
        String status,
        boolean isPremium,
        UUID instructorId,
        String instructorName,
        BigDecimal averageRating,
        int enrollmentCount,
        Instant createdAt,
        Instant updatedAt
) {}
