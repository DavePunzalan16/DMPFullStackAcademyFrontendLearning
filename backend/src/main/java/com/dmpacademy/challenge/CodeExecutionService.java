package com.dmpacademy.challenge;

import com.dmpacademy.challenge.dto.ExecutionResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Executes student code in a sandboxed process.
 * In production, this would use Docker containers for isolation.
 * For development, we use ProcessBuilder with timeout enforcement.
 */
@Slf4j
@Service
public class CodeExecutionService {

    public ExecutionResultResponse executeCode(
            String code,
            String language,
            List<ChallengeTestCase> testCases,
            int timeoutSeconds
    ) {
        List<ExecutionResultResponse.TestCaseResult> results = new ArrayList<>();
        int passedCount = 0;

        for (int i = 0; i < testCases.size(); i++) {
            ChallengeTestCase testCase = testCases.get(i);
            ExecutionResultResponse.TestCaseResult result = executeTestCase(code, language, testCase, timeoutSeconds, i);
            results.add(result);
            if (result.passed()) passedCount++;
        }

        boolean allPassed = passedCount == testCases.size();

        return new ExecutionResultResponse(allPassed, passedCount, testCases.size(), results, null);
    }

    private ExecutionResultResponse.TestCaseResult executeTestCase(
            String code, String language, ChallengeTestCase testCase, int timeoutSeconds, int index
    ) {
        try {
            Path tempDir = Files.createTempDirectory("dmp-code-");
            String fileName = getFileName(language);
            Path codeFile = tempDir.resolve(fileName);
            Files.writeString(codeFile, code);

            ProcessBuilder pb = getProcessBuilder(language, codeFile);
            pb.directory(tempDir.toFile());
            pb.redirectErrorStream(false);

            Process process = pb.start();

            // Write input to stdin
            if (testCase.getInput() != null && !testCase.getInput().isBlank()) {
                try (OutputStream os = process.getOutputStream()) {
                    os.write(testCase.getInput().getBytes());
                    os.flush();
                }
            } else {
                process.getOutputStream().close();
            }

            boolean completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                return new ExecutionResultResponse.TestCaseResult(index, false, null, testCase.getExpectedOutput(), "Execution timed out");
            }

            String stdout = new String(process.getInputStream().readAllBytes()).trim();
            String stderr = new String(process.getErrorStream().readAllBytes()).trim();

            // Clean up
            Files.deleteIfExists(codeFile);
            Files.deleteIfExists(tempDir);

            if (process.exitValue() != 0) {
                return new ExecutionResultResponse.TestCaseResult(index, false, stderr, testCase.getExpectedOutput(), stderr);
            }

            boolean passed = stdout.equals(testCase.getExpectedOutput().trim());
            return new ExecutionResultResponse.TestCaseResult(index, passed, stdout, testCase.getExpectedOutput().trim(), passed ? null : "Output mismatch");

        } catch (Exception e) {
            log.error("Code execution error: ", e);
            return new ExecutionResultResponse.TestCaseResult(index, false, null, testCase.getExpectedOutput(), "Execution error: " + e.getMessage());
        }
    }

    private String getFileName(String language) {
        return switch (language.toLowerCase()) {
            case "java" -> "Solution.java";
            case "python" -> "solution.py";
            case "javascript" -> "solution.js";
            default -> "solution.txt";
        };
    }

    private ProcessBuilder getProcessBuilder(String language, Path codeFile) {
        return switch (language.toLowerCase()) {
            case "python" -> new ProcessBuilder("python", codeFile.toString());
            case "javascript" -> new ProcessBuilder("node", codeFile.toString());
            case "java" -> new ProcessBuilder("java", codeFile.toString());
            default -> new ProcessBuilder("echo", "Unsupported language");
        };
    }
}
