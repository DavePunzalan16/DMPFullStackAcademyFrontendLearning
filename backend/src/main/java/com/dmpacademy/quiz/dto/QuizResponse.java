package com.dmpacademy.quiz.dto;

import java.util.List;
import java.util.UUID;

public record QuizResponse(
        UUID id,
        UUID lessonId,
        String title,
        int passingScore,
        int questionCount,
        List<QuestionResponse> questions
) {
    public record QuestionResponse(
            UUID id,
            String questionText,
            int orderIndex,
            List<OptionResponse> options
    ) {}

    public record OptionResponse(
            UUID id,
            String optionText,
            int orderIndex
            // NOTE: isCorrect is NOT included — students don't see correct answers
    ) {}
}
