package com.dmpacademy.challenge;

import com.dmpacademy.challenge.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Challenges", description = "Coding challenge creation and submission")
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("/api/v1/lessons/{lessonId}/challenge")
    @Operation(summary = "Create a coding challenge for a lesson (Instructor)")
    public ResponseEntity<ChallengeResponse> createChallenge(
            @PathVariable UUID lessonId,
            @Valid @RequestBody ChallengeCreateRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        ChallengeResponse response = challengeService.createChallenge(lessonId, request, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/lessons/{lessonId}/challenge")
    @Operation(summary = "Get coding challenge for a lesson")
    public ResponseEntity<ChallengeResponse> getChallenge(@PathVariable UUID lessonId) {
        ChallengeResponse response = challengeService.getChallenge(lessonId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/challenges/{challengeId}/submit")
    @Operation(summary = "Submit code solution (Student, enrolled course)")
    public ResponseEntity<ExecutionResultResponse> submitCode(
            @PathVariable UUID challengeId,
            @Valid @RequestBody CodeSubmissionRequest request,
            Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        ExecutionResultResponse response = challengeService.submitCode(challengeId, request, studentId);
        return ResponseEntity.ok(response);
    }
}
