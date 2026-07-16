package com.dmpacademy.gamification;

import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.event.XpAwardedEvent;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Centralized XP award service. All modules (lesson, quiz, challenge, quest)
 * call this service to award XP rather than duplicating level/XP logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XpAwardService {

    private final XpTransactionRepository xpTransactionRepository;
    private final LevelThresholdRepository levelThresholdRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Awards XP to a student. Idempotent — duplicate awards for the same
     * (student, sourceType, sourceId) are silently ignored.
     *
     * @param studentId  the student receiving XP
     * @param amount     XP amount to award
     * @param sourceType type: LESSON, QUIZ, CHALLENGE, QUEST
     * @param sourceId   ID of the entity that triggered the award
     * @return true if XP was actually awarded (first time), false if duplicate
     */
    @Transactional
    public boolean awardXp(UUID studentId, int amount, String sourceType, UUID sourceId) {
        // Idempotency check — prevent duplicate awards
        if (xpTransactionRepository.existsByStudentIdAndSourceTypeAndSourceId(studentId, sourceType, sourceId)) {
            log.debug("XP already awarded to {} for {} {}", studentId, sourceType, sourceId);
            return false;
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // Record the transaction
        XpTransaction transaction = XpTransaction.builder()
                .student(student)
                .amount(amount)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .build();
        xpTransactionRepository.save(transaction);

        // Update user's total XP atomically
        int newTotal = student.getXpTotal() + amount;
        student.setXpTotal(newTotal);

        // Recalculate level
        levelThresholdRepository.findHighestLevelForXp(newTotal)
                .ifPresent(threshold -> {
                    if (threshold.getLevel() > student.getLevel()) {
                        log.info("Student {} leveled up from {} to {}!", studentId, student.getLevel(), threshold.getLevel());
                        student.setLevel(threshold.getLevel());
                    }
                });

        userRepository.save(student);

        // Publish event for cross-cutting concerns (badges, notifications, analytics)
        eventPublisher.publishEvent(new XpAwardedEvent(studentId, sourceId, amount, sourceType, newTotal));

        log.info("Awarded {} XP to student {} for {} {} (total: {})", amount, studentId, sourceType, sourceId, newTotal);
        return true;
    }
}
