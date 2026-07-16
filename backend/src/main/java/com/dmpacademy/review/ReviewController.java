package com.dmpacademy.review;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.review.dto.ReviewCreateRequest;
import com.dmpacademy.review.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Course reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/v1/courses/{courseId}/reviews")
    @Operation(summary = "Submit a review (Student, enrolled)")
    public ResponseEntity<ReviewResponse> submitReview(
            @PathVariable UUID courseId,
            @Valid @RequestBody ReviewCreateRequest request,
            Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        ReviewResponse response = reviewService.submitReview(courseId, request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/courses/{courseId}/reviews")
    @Operation(summary = "List approved reviews for a course (Public)")
    public ResponseEntity<PageResponse<ReviewResponse>> listReviews(
            @PathVariable UUID courseId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PageResponse<ReviewResponse> response = reviewService.listApprovedReviews(courseId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/v1/reviews/{id}/moderate")
    @Operation(summary = "Moderate a review (Admin: APPROVE, REJECT, REMOVE)")
    public ResponseEntity<Void> moderateReview(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ) {
        reviewService.moderateReview(id, body.get("action"));
        return ResponseEntity.noContent().build();
    }
}
