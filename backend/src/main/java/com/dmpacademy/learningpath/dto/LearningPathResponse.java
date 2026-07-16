package com.dmpacademy.learningpath.dto;

import java.util.List;
import java.util.UUID;

public record LearningPathResponse(
        UUID id, String name, String description, List<CourseItem> courses
) {
    public record CourseItem(UUID courseId, String title, int orderIndex) {}
}
