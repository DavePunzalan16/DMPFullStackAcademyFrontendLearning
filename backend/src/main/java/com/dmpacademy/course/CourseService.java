package com.dmpacademy.course;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.course.dto.CourseCreateRequest;
import com.dmpacademy.course.dto.CourseResponse;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request, UUID instructorId) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ValidationException("Category not found with id: " + request.categoryId()));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", instructorId));

        Course course = Course.builder()
                .title(request.title().trim())
                .description(request.description().trim())
                .category(category)
                .difficulty(request.difficulty())
                .status(CourseStatus.DRAFT)
                .isPremium(request.isPremium() != null && request.isPremium())
                .instructor(instructor)
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created: {} by instructor {}", saved.getId(), instructorId);
        return courseMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public CourseResponse updateCourse(UUID courseId, CourseCreateRequest request, UUID instructorId) {
        Course course = getCourseOrThrow(courseId);
        verifyOwnership(course, instructorId);

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ValidationException("Category not found with id: " + request.categoryId()));

        course.setTitle(request.title().trim());
        course.setDescription(request.description().trim());
        course.setCategory(category);
        course.setDifficulty(request.difficulty());
        course.setPremium(request.isPremium() != null && request.isPremium());

        Course saved = courseRepository.save(course);
        return courseMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public CourseResponse publishCourse(UUID courseId, UUID instructorId) {
        Course course = getCourseOrThrow(courseId);
        verifyOwnership(course, instructorId);

        if (course.getStatus() == CourseStatus.PUBLISHED) {
            return courseMapper.toResponse(course);
        }

        // Validation: course must have at least one module with at least one lesson
        // This will be enforced once Module entity is created (Task 11)
        // For now, just change status
        course.setStatus(CourseStatus.PUBLISHED);
        Course saved = courseRepository.save(course);
        log.info("Course published: {}", saved.getId());
        return courseMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void softDeleteCourse(UUID courseId) {
        Course course = getCourseOrThrow(courseId);
        course.setDeleted(true);
        courseRepository.save(course);
        log.info("Course soft-deleted: {}", courseId);
    }

    public CourseResponse getCourseById(UUID courseId) {
        Course course = getCourseOrThrow(courseId);
        return courseMapper.toResponse(course);
    }

    public PageResponse<CourseResponse> listPublishedCourses(Pageable pageable) {
        Page<CourseResponse> page = courseRepository.findPublishedCourses(pageable)
                .map(courseMapper::toResponse);
        return PageResponse.from(page);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    public PageResponse<CourseResponse> listInstructorCourses(UUID instructorId, Pageable pageable) {
        Page<CourseResponse> page = courseRepository.findByInstructorIdAndDeletedFalse(instructorId, pageable)
                .map(courseMapper::toResponse);
        return PageResponse.from(page);
    }

    public PageResponse<CourseResponse> searchCourses(String query, UUID categoryId, Difficulty difficulty, Pageable pageable) {
        Page<CourseResponse> page = courseRepository.searchCourses(query, categoryId, difficulty, pageable)
                .map(courseMapper::toResponse);
        return PageResponse.from(page);
    }

    private Course getCourseOrThrow(UUID courseId) {
        return courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
    }

    private void verifyOwnership(Course course, UUID instructorId) {
        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
    }
}
