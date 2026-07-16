package com.dmpacademy.progress;

import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.gamification.LevelThresholdRepository;
import com.dmpacademy.progress.dto.DashboardResponse;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LevelThresholdRepository levelThresholdRepository;

    public DashboardResponse getStudentDashboard(UUID studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // XP info
        int maxLevel = levelThresholdRepository.findMaxLevel();
        boolean maxReached = user.getLevel() >= maxLevel;
        int xpForNext = maxReached ? 0 : levelThresholdRepository.findNextLevel(user.getLevel())
                .map(lt -> lt.getXpRequired() - user.getXpTotal())
                .orElse(0);

        // Streak
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        boolean activityToday = today.equals(user.getLastActivityDate());
        int streak = user.getStreakCount();
        if (user.getLastActivityDate() != null && !activityToday && !user.getLastActivityDate().equals(today.minusDays(1))) {
            streak = 0;
        }

        // Enrolled courses
        List<DashboardResponse.EnrolledCourseItem> courses = enrollmentRepository
                .findByStudentIdWithCourse(studentId, PageRequest.of(0, 50))
                .map(e -> new DashboardResponse.EnrolledCourseItem(
                        e.getCourse().getId(), e.getCourse().getTitle(),
                        e.getCompletionPercentage(), e.getEnrolledAt()))
                .getContent();

        // Recent activity (last 10 lesson completions)
        List<DashboardResponse.RecentActivityItem> recentActivity = lessonProgressRepository
                .findRecentByStudent(studentId)
                .stream()
                .limit(10)
                .map(lp -> new DashboardResponse.RecentActivityItem(
                        "LESSON", lp.getLesson().getTitle(),
                        lp.getLesson().getModule().getCourse().getTitle(),
                        lp.getCompletedAt()))
                .toList();

        return new DashboardResponse(
                user.getXpTotal(), user.getLevel(), Math.max(0, xpForNext),
                streak, user.getLongestStreak(), activityToday,
                courses, recentActivity
        );
    }
}
