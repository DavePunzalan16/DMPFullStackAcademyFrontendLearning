package com.dmpacademy.course;

import com.dmpacademy.course.dto.CourseResponse;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCategory().getId(),
                course.getCategory().getName(),
                course.getDifficulty().name(),
                course.getStatus().name(),
                course.isPremium(),
                course.getInstructor().getId(),
                course.getInstructor().getDisplayName(),
                course.getAverageRating(),
                course.getEnrollmentCount(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
}
