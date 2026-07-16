package com.dmpacademy.badge.dto;

import java.time.Instant;
import java.util.UUID;

public record BadgeResponse(
        UUID id,
        String name,
        String description,
        String iconRef,
        String criteriaDescription,
        Instant awardedAt
) {}
