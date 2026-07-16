package com.dmpacademy.lesson;

import com.dmpacademy.lesson.dto.LessonCreateRequest;
import com.dmpacademy.lesson.dto.LessonResponse;
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
@Tag(name = "Lessons", description = "Lesson management within modules")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/api/v1/modules/{moduleId}/lessons")
    @Operation(summary = "Create a lesson within a module (Instructor, own course)")
    public ResponseEntity<LessonResponse> createLesson(
            @PathVariable UUID moduleId,
            @Valid @RequestBody LessonCreateRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        LessonResponse response = lessonService.createLesson(moduleId, request, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/lessons/{id}")
    @Operation(summary = "Get lesson content")
    public ResponseEntity<LessonResponse> getLesson(@PathVariable UUID id) {
        LessonResponse response = lessonService.getLesson(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/v1/lessons/{id}")
    @Operation(summary = "Update a lesson (Instructor, own course)")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable UUID id,
            @Valid @RequestBody LessonCreateRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        LessonResponse response = lessonService.updateLesson(id, request, instructorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/v1/lessons/{id}")
    @Operation(summary = "Delete a lesson (Instructor, own course)")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        lessonService.deleteLesson(id, instructorId);
        return ResponseEntity.noContent().build();
    }
}
