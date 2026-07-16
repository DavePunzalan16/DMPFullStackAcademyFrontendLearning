package com.dmpacademy.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        UUID referenceId,
        boolean isRead,
        Instant createdAt
) {}
