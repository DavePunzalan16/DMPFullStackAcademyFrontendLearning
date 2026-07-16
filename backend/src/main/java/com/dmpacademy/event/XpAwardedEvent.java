package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class XpAwardedEvent extends DomainEvent {

    private final int amount;
    private final String sourceType;
    private final int newTotalXp;

    public XpAwardedEvent(UUID userId, UUID sourceId, int amount, String sourceType, int newTotalXp) {
        super(userId, sourceId, "XP_AWARDED");
        this.amount = amount;
        this.sourceType = sourceType;
        this.newTotalXp = newTotalXp;
    }
}
