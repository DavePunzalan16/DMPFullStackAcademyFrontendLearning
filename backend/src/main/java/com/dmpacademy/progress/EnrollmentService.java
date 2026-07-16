package com.dmpacademy.progress;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.DuplicateResourceException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.course.Course;
import com.dmpacademy.course.CourseRepository;
import com.dmpacademy.course.CourseStatus;
import com.dmpacademy.event.CourseEnrolledEvent;
import com.dmpacademy.progress.dto.EnrollmentResponse;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public EnrollmentResponse enroll(UUID courseId, UUID studentId) {
        // Verify course exists and is published
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }

        // Check premium access
        if (course.isPremium()) {
            throw new AccessDeniedException("Premium access is required to enroll in this course");
        }

        // Check duplicate enrollment
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new DuplicateResourceException("You are already enrolled in this course");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // Create enrollment
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .completionPercentage(0)
                .completed(false)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        // Increment course enrollment count
        course.setEnrollmentCount(course.getEnrollmentCount() + 1);
        courseRepository.save(course);

        // Publish event
        eventPublisher.publishEvent(new CourseEnrolledEvent(studentId, courseId));

        log.info("Student {} enrolled in course {}", studentId, courseId);
        return toResponse(saved);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public PageResponse<EnrollmentResponse> listEnrollments(UUID studentId, Pageable pageable) {
        Page<EnrollmentResponse> page = enrollmentRepository.findByStudentIdWithCourse(studentId, pageable)
                .map(this::toResponse);
        return PageResponse.from(page);
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getEnrolledAt(),
                enrollment.isCompleted(),
                enrollment.getCompletionPercentage()
        );
    }
}
