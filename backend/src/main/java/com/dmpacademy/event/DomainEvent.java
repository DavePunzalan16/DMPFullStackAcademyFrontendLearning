package com.dmpacademy.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public abstract class DomainEvent {

    private final UUID userId;
    private final UUID entityId;
    private final String eventType;
    private final Instant timestamp;

    protected DomainEvent(UUID userId, UUID entityId, String eventType) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.timestamp = Instant.now();
    }
}
