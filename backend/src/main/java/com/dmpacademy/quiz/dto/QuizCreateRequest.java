package com.dmpacademy.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record QuizCreateRequest(
        @NotBlank(message = "Quiz title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @Min(value = 1, message = "Passing score must be at least 1")
        @Max(value = 100, message = "Passing score must not exceed 100")
        int passingScore,

        @NotEmpty(message = "At least one question is required")
        @Size(max = 50, message = "Maximum 50 questions per quiz")
        @Valid
        List<QuestionRequest> questions
) {
    public record QuestionRequest(
            @NotBlank(message = "Question text is required")
            @Size(max = 2000, message = "Question text must not exceed 2000 characters")
            String questionText,

            @NotEmpty(message = "At least 2 options required")
            @Size(min = 2, max = 6, message = "Each question must have between 2 and 6 options")
            @Valid
            List<OptionRequest> options
    ) {}

    public record OptionRequest(
            @NotBlank(message = "Option text is required")
            @Size(max = 500, message = "Option text must not exceed 500 characters")
            String optionText,

            boolean isCorrect
    ) {}
}
