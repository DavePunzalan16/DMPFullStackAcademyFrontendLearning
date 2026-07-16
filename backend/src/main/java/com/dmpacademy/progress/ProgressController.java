package com.dmpacademy.progress;

import com.dmpacademy.progress.dto.ProgressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Progress", description = "Lesson completion and course progress tracking")
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping("/api/v1/lessons/{lessonId}/complete")
    @Operation(summary = "Mark a lesson as complete (Student, enrolled course)")
    public ResponseEntity<ProgressResponse> markLessonComplete(
            @PathVariable UUID lessonId,
            Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        ProgressResponse response = progressService.markLessonComplete(lessonId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/courses/{courseId}/progress")
    @Operation(summary = "Get progress for an enrolled course (Student)")
    public ResponseEntity<ProgressResponse> getCourseProgress(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        ProgressResponse response = progressService.getProgress(courseId, studentId);
        return ResponseEntity.ok(response);
    }
}
