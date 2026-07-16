package com.dmpacademy.progress;

import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.course.Course;
import com.dmpacademy.course.CourseRepository;
import com.dmpacademy.event.CourseCompletedEvent;
import com.dmpacademy.event.LessonCompletedEvent;
import com.dmpacademy.lesson.Lesson;
import com.dmpacademy.lesson.LessonRepository;
import com.dmpacademy.progress.dto.ProgressResponse;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public ProgressResponse markLessonComplete(UUID lessonId, UUID studentId) {
        // Get lesson and find course
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        UUID courseId = lesson.getModule().getCourse().getId();

        // Verify enrollment
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new AccessDeniedException("You are not enrolled in this course"));

        // Check if already completed (idempotent)
        if (lessonProgressRepository.existsByStudentIdAndLessonId(studentId, lessonId)) {
            // Already completed — return current progress without error
            return getProgress(courseId, studentId);
        }

        // Record completion
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        LessonProgress progress = LessonProgress.builder()
                .student(student)
                .lesson(lesson)
                .build();

        lessonProgressRepository.save(progress);

        // Recalculate completion percentage
        long totalLessons = lessonRepository.countByCourseId(courseId);
        long completedLessons = lessonProgressRepository.countCompletedLessonsInCourse(studentId, courseId);

        int percentage = totalLessons == 0 ? 0 : (int) ((completedLessons * 100) / totalLessons);
        enrollment.setCompletionPercentage(percentage);

        // Check for course completion (100%)
        if (completedLessons >= totalLessons && totalLessons > 0) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(Instant.now());
            log.info("Student {} completed course {}", studentId, courseId);
            eventPublisher.publishEvent(new CourseCompletedEvent(studentId, courseId));
        }

        enrollmentRepository.save(enrollment);

        // Publish lesson completed event
        eventPublisher.publishEvent(new LessonCompletedEvent(studentId, lessonId, courseId));

        log.info("Student {} completed lesson {} ({}/{})", studentId, lessonId, completedLessons, totalLessons);
        return getProgress(courseId, studentId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    public ProgressResponse getProgress(UUID courseId, UUID studentId) {
        // Verify enrollment
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AccessDeniedException("You are not enrolled in this course");
        }

        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        // Get all lesson IDs for this course
        List<UUID> allLessonIds = lessonRepository.findAllLessonIdsByCourseId(courseId);
        long totalLessons = allLessonIds.size();

        // Get completed lesson IDs
        List<UUID> completedIds = allLessonIds.isEmpty()
                ? List.of()
                : lessonProgressRepository.findCompletedLessonIds(studentId, allLessonIds);

        // Build completed lessons with timestamps
        List<LessonProgress> progressRecords = lessonProgressRepository.findByStudentAndCourse(studentId, courseId);
        List<ProgressResponse.CompletedLesson> completedLessons = progressRecords.stream()
                .map(lp -> new ProgressResponse.CompletedLesson(lp.getLesson().getId(), lp.getCompletedAt()))
                .toList();

        // Calculate remaining
        List<UUID> remainingIds = new ArrayList<>(allLessonIds);
        remainingIds.removeAll(completedIds);

        int percentage = totalLessons == 0 ? 0 : (int) ((completedIds.size() * 100) / totalLessons);

        return new ProgressResponse(
                courseId,
                course.getTitle(),
                percentage,
                completedIds.size(),
                (int) totalLessons,
                completedLessons,
                remainingIds
        );
    }
}
