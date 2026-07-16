package com.dmpacademy.quiz.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Map;
import java.util.UUID;

public record QuizSubmissionRequest(
        @NotEmpty(message = "Answers cannot be empty")
        Map<UUID, UUID> answers  // questionId → selectedOptionId
) {}
