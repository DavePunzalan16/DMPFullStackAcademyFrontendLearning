package com.dmpacademy.gamification;

import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.event.StreakMilestoneEvent;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

/**
 * Manages daily learning streaks. Evaluated lazily on each activity completion
 * rather than via a background scheduler.
 *
 * Logic:
 * - If last_activity_date == today (UTC): no change (already counted today)
 * - If last_activity_date == yesterday (UTC): increment streak
 * - If last_activity_date < yesterday or null: reset streak to 1
 * - Update longest_streak if current exceeds it
 * - Check milestones (7, 30, 100) and publish event
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreakService {

    private static final Set<Integer> MILESTONES = Set.of(7, 30, 100);

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void updateStreak(UUID studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        LocalDate lastActivity = student.getLastActivityDate();

        // Already counted today — no-op
        if (today.equals(lastActivity)) {
            return;
        }

        int newStreak;

        if (lastActivity != null && lastActivity.equals(today.minusDays(1))) {
            // Consecutive day — increment
            newStreak = student.getStreakCount() + 1;
        } else {
            // Gap or first activity ever — reset to 1
            newStreak = 1;
        }

        student.setStreakCount(newStreak);
        student.setLastActivityDate(today);

        // Update longest streak
        if (newStreak > student.getLongestStreak()) {
            student.setLongestStreak(newStreak);
        }

        userRepository.save(student);

        // Check milestones
        if (MILESTONES.contains(newStreak)) {
            log.info("Student {} reached streak milestone: {} days!", studentId, newStreak);
            eventPublisher.publishEvent(new StreakMilestoneEvent(studentId, newStreak));
        }

        log.debug("Streak updated for student {}: {} days", studentId, newStreak);
    }
}
