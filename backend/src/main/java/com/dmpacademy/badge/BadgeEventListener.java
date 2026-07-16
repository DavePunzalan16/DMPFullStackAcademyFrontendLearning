package com.dmpacademy.badge;

import com.dmpacademy.event.CourseCompletedEvent;
import com.dmpacademy.event.StreakMilestoneEvent;
import com.dmpacademy.event.XpAwardedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class BadgeEventListener {

    private final BadgeService badgeService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onStreakMilestone(StreakMilestoneEvent event) {
        try {
            badgeService.evaluateAndAwardBadge(event.getUserId(), "STREAK_MILESTONE", event.getStreakDays());
        } catch (Exception e) {
            log.error("Failed to evaluate badge for streak milestone: {}", e.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onCourseCompleted(CourseCompletedEvent event) {
        try {
            // Count total completed courses for this student to check milestone badges
            // For now, we trigger with value 1 (first course badge)
            badgeService.evaluateAndAwardBadge(event.getUserId(), "COURSES_COMPLETED", 1);
        } catch (Exception e) {
            log.error("Failed to evaluate badge for course completion: {}", e.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onXpAwarded(XpAwardedEvent event) {
        try {
            // Check XP threshold badges (1000, 5000, 10000)
            int xp = event.getNewTotalXp();
            if (xp >= 10000) badgeService.evaluateAndAwardBadge(event.getUserId(), "XP_THRESHOLD", 10000);
            else if (xp >= 5000) badgeService.evaluateAndAwardBadge(event.getUserId(), "XP_THRESHOLD", 5000);
            else if (xp >= 1000) badgeService.evaluateAndAwardBadge(event.getUserId(), "XP_THRESHOLD", 1000);
        } catch (Exception e) {
            log.error("Failed to evaluate badge for XP threshold: {}", e.getMessage());
        }
    }
}
