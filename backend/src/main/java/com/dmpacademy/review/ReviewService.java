package com.dmpacademy.review;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.DuplicateResourceException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.course.Course;
import com.dmpacademy.course.CourseRepository;
import com.dmpacademy.progress.EnrollmentRepository;
import com.dmpacademy.review.dto.ReviewCreateRequest;
import com.dmpacademy.review.dto.ReviewResponse;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final CourseReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public ReviewResponse submitReview(UUID courseId, ReviewCreateRequest request, UUID studentId) {
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AccessDeniedException("You must be enrolled to review this course");
        }
        if (reviewRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("You have already reviewed this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        CourseReview review = CourseReview.builder()
                .student(student).course(course).rating(request.rating())
                .comment(request.comment()).status("PENDING").build();

        CourseReview saved = reviewRepository.save(review);
        return mapToResponse(saved, student.getDisplayName());
    }

    public PageResponse<ReviewResponse> listApprovedReviews(UUID courseId, Pageable pageable) {
        Page<ReviewResponse> page = reviewRepository.findByCourseIdAndStatus(courseId, "APPROVED", pageable)
                .map(r -> mapToResponse(r, r.getStudent().getDisplayName()));
        return PageResponse.from(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void moderateReview(UUID reviewId, String action) {
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        switch (action.toUpperCase()) {
            case "APPROVE" -> review.setStatus("APPROVED");
            case "REJECT" -> review.setStatus("REJECTED");
            case "REMOVE" -> { reviewRepository.delete(review); recalculateRating(review.getCourse().getId()); return; }
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
        reviewRepository.save(review);

        if ("APPROVE".equalsIgnoreCase(action)) {
            recalculateRating(review.getCourse().getId());
        }
    }

    private void recalculateRating(UUID courseId) {
        BigDecimal avg = reviewRepository.calculateAverageRating(courseId);
        courseRepository.findByIdAndDeletedFalse(courseId).ifPresent(course -> {
            course.setAverageRating(avg);
            courseRepository.save(course);
        });
    }

    private ReviewResponse mapToResponse(CourseReview r, String studentName) {
        return new ReviewResponse(r.getId(), r.getCourse().getId(), studentName,
                r.getRating(), r.getComment(), r.getStatus(), r.getCreatedAt());
    }
}
