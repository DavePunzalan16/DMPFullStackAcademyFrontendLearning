package com.dmpacademy.progress.dto;

import java.time.Instant;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        Instant enrolledAt,
        boolean completed,
        int completionPercentage
) {}
