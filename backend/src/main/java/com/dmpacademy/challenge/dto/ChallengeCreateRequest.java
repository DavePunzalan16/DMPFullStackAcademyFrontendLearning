package com.dmpacademy.challenge.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChallengeCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200) String title,

        @NotBlank(message = "Description is required")
        @Size(max = 5000) String description,

        @NotBlank(message = "Starter code is required")
        String starterCode,

        @NotBlank(message = "Language is required")
        String language,

        Integer timeoutSeconds,

        @NotEmpty(message = "At least one test case is required")
        @Valid List<TestCaseRequest> testCases
) {
    public record TestCaseRequest(
            @NotBlank(message = "Input is required") String input,
            @NotBlank(message = "Expected output is required") String expectedOutput,
            boolean isHidden
    ) {}
}
