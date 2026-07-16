package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class QuizPassedEvent extends DomainEvent {

    private final int score;

    public QuizPassedEvent(UUID userId, UUID quizId, int score) {
        super(userId, quizId, "QUIZ_PASSED");
        this.score = score;
    }
}
