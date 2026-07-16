package com.dmpacademy.quest;

import com.dmpacademy.event.ChallengeCompletedEvent;
import com.dmpacademy.event.LessonCompletedEvent;
import com.dmpacademy.event.QuizPassedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestEventListener {

    private final QuestService questService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onLessonCompleted(LessonCompletedEvent event) {
        try {
            questService.incrementObjectiveProgress(event.getUserId(), "LESSON_COMPLETION");
        } catch (Exception e) {
            log.error("Quest progress update failed for lesson: {}", e.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onQuizPassed(QuizPassedEvent event) {
        try {
            questService.incrementObjectiveProgress(event.getUserId(), "QUIZ_PASS");
        } catch (Exception e) {
            log.error("Quest progress update failed for quiz: {}", e.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onChallengeCompleted(ChallengeCompletedEvent event) {
        try {
            questService.incrementObjectiveProgress(event.getUserId(), "CHALLENGE_COMPLETION");
        } catch (Exception e) {
            log.error("Quest progress update failed for challenge: {}", e.getMessage());
        }
    }
}
