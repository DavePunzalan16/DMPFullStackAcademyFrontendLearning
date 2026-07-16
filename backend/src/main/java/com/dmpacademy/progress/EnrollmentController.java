package com.dmpacademy.progress;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.progress.dto.EnrollmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Course enrollment management")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/api/v1/courses/{courseId}/enroll")
    @Operation(summary = "Enroll in a course (Student only)")
    public ResponseEntity<EnrollmentResponse> enroll(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        EnrollmentResponse response = enrollmentService.enroll(courseId, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/enrollments")
    @Operation(summary = "List my enrolled courses (Student only)")
    public ResponseEntity<PageResponse<EnrollmentResponse>> listEnrollments(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        PageResponse<EnrollmentResponse> response = enrollmentService.listEnrollments(studentId, pageable);
        return ResponseEntity.ok(response);
    }
}
