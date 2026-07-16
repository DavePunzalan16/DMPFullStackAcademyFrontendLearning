package com.dmpacademy.challenge.dto;

import java.util.List;

public record ExecutionResultResponse(
        boolean allPassed,
        int passedCount,
        int totalCount,
        List<TestCaseResult> results,
        String error
) {
    public record TestCaseResult(
            int testCaseIndex,
            boolean passed,
            String actualOutput,
            String expectedOutput,
            String error
    ) {}
}
