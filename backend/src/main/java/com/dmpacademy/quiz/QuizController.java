package com.dmpacademy.quiz;

import com.dmpacademy.quiz.dto.*;
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
@Tag(name = "Quizzes", description = "Quiz creation and submission")
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/api/v1/lessons/{lessonId}/quiz")
    @Operation(summary = "Create a quiz for a lesson (Instructor, own course)")
    public ResponseEntity<QuizResponse> createQuiz(
            @PathVariable UUID lessonId,
            @Valid @RequestBody QuizCreateRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        QuizResponse response = quizService.createQuiz(lessonId, request, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/lessons/{lessonId}/quiz")
    @Operation(summary = "Get quiz for a lesson (without correct answers)")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable UUID lessonId) {
        QuizResponse response = quizService.getQuiz(lessonId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/quizzes/{quizId}/submit")
    @Operation(summary = "Submit quiz answers (Student, enrolled course)")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable UUID quizId,
            @Valid @RequestBody QuizSubmissionRequest request,
            Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        QuizResultResponse response = quizService.submitQuiz(quizId, request, studentId);
        return ResponseEntity.ok(response);
    }
}
