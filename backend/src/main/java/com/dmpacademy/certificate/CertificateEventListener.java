package com.dmpacademy.certificate;

import com.dmpacademy.event.CourseCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class CertificateEventListener {

    private final CertificateService certificateService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onCourseCompleted(CourseCompletedEvent event) {
        try {
            certificateService.generateCertificate(event.getUserId(), event.getEntityId());
        } catch (Exception e) {
            log.error("Failed to generate certificate for student {} course {}: {}",
                    event.getUserId(), event.getEntityId(), e.getMessage());
        }
    }
}
