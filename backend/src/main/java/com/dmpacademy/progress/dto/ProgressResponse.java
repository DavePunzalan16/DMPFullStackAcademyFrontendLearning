package com.dmpacademy.progress.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProgressResponse(
        UUID courseId,
        String courseTitle,
        int completionPercentage,
        int completedCount,
        int totalCount,
        List<CompletedLesson> completedLessons,
        List<UUID> remainingLessonIds
) {
    public record CompletedLesson(UUID lessonId, Instant completedAt) {}
}
