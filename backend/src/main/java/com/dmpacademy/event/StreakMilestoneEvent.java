package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StreakMilestoneEvent extends DomainEvent {

    private final int streakDays;

    public StreakMilestoneEvent(UUID userId, int streakDays) {
        super(userId, userId, "STREAK_MILESTONE");
        this.streakDays = streakDays;
    }
}
