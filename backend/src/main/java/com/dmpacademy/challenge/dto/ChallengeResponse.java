package com.dmpacademy.challenge.dto;

import java.util.List;
import java.util.UUID;

public record ChallengeResponse(
        UUID id,
        UUID lessonId,
        String title,
        String description,
        String starterCode,
        String language,
        int timeoutSeconds,
        List<TestCaseResponse> testCases
) {
    public record TestCaseResponse(UUID id, String input, String expectedOutput, int orderIndex) {}
}
