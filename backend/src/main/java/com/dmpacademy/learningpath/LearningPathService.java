package com.dmpacademy.learningpath;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.course.Course;
import com.dmpacademy.course.CourseRepository;
import com.dmpacademy.course.CourseStatus;
import com.dmpacademy.learningpath.dto.LearningPathRequest;
import com.dmpacademy.learningpath.dto.LearningPathResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final CourseRepository courseRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LearningPathResponse create(LearningPathRequest request) {
        LearningPath lp = LearningPath.builder()
                .name(request.name().trim())
                .description(request.description())
                .build();

        List<LearningPathCourse> courses = new ArrayList<>();
        for (int i = 0; i < request.courseIds().size(); i++) {
            Course course = courseRepository.findByIdAndDeletedFalse(request.courseIds().get(i))
                    .orElseThrow(() -> new ValidationException("Course not found or not published"));
            if (course.getStatus() != CourseStatus.PUBLISHED) {
                throw new ValidationException("Course '" + course.getTitle() + "' is not published");
            }
            courses.add(LearningPathCourse.builder().learningPath(lp).course(course).orderIndex(i + 1).build());
        }
        lp.setCourses(courses);

        LearningPath saved = learningPathRepository.save(lp);
        return mapToResponse(saved);
    }

    public PageResponse<LearningPathResponse> list(Pageable pageable) {
        Page<LearningPathResponse> page = learningPathRepository.findAllWithPublishedCourses(pageable)
                .map(this::mapToResponse);
        return PageResponse.from(page);
    }

    public LearningPathResponse getById(UUID id) {
        LearningPath lp = learningPathRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new ResourceNotFoundException("LearningPath", "id", id));
        return mapToResponse(lp);
    }

    private LearningPathResponse mapToResponse(LearningPath lp) {
        return new LearningPathResponse(lp.getId(), lp.getName(), lp.getDescription(),
                lp.getCourses().stream()
                        .map(c -> new LearningPathResponse.CourseItem(c.getCourse().getId(), c.getCourse().getTitle(), c.getOrderIndex()))
                        .toList());
    }
}
