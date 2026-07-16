package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BadgeAwardedEvent extends DomainEvent {

    private final String badgeName;

    public BadgeAwardedEvent(UUID userId, UUID badgeId, String badgeName) {
        super(userId, badgeId, "BADGE_AWARDED");
        this.badgeName = badgeName;
    }
}
