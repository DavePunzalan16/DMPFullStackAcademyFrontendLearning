package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChallengeCompletedEvent extends DomainEvent {

    public ChallengeCompletedEvent(UUID userId, UUID challengeId) {
        super(userId, challengeId, "CHALLENGE_COMPLETED");
    }
}
