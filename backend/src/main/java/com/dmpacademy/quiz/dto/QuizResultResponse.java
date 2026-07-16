package com.dmpacademy.quiz.dto;

import java.util.List;
import java.util.UUID;

public record QuizResultResponse(
        int score,
        boolean passed,
        int totalQuestions,
        int correctAnswers,
        List<QuestionResult> questionResults
) {
    public record QuestionResult(
            UUID questionId,
            boolean correct,
            UUID correctOptionId
    ) {}
}
