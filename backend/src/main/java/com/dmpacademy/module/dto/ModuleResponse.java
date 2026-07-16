package com.dmpacademy.module.dto;

import java.time.Instant;
import java.util.UUID;

public record ModuleResponse(
        UUID id,
        UUID courseId,
        String title,
        int orderIndex,
        Instant createdAt
) {}
