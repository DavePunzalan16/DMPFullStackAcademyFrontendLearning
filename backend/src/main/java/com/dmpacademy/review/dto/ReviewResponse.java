package com.dmpacademy.review.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id, UUID courseId, String studentName, int rating,
        String comment, String status, Instant createdAt
) {}
