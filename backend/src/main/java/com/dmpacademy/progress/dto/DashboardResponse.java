package com.dmpacademy.progress.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        int xpTotal,
        int currentLevel,
        int xpForNextLevel,
        int streakCount,
        int longestStreak,
        boolean activityCompletedToday,
        List<EnrolledCourseItem> enrolledCourses,
        List<RecentActivityItem> recentActivity
) {
    public record EnrolledCourseItem(
            UUID courseId, String courseTitle, int completionPercentage, Instant enrolledAt
    ) {}

    public record RecentActivityItem(
            String activityType, String title, String courseTitle, Instant completedAt
    ) {}
}
