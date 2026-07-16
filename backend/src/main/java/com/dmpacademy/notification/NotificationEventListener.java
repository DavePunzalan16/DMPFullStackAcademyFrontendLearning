package com.dmpacademy.notification;

import com.dmpacademy.event.BadgeAwardedEvent;
import com.dmpacademy.event.CertificateIssuedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onBadgeAwarded(BadgeAwardedEvent event) {
        try {
            notificationService.createNotification(
                    event.getUserId(),
                    "BADGE_EARNED",
                    "You earned the badge: " + event.getBadgeName(),
                    event.getEntityId()
            );
        } catch (Exception e) {
            log.error("Failed to create notification for badge: {}", e.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onCertificateIssued(CertificateIssuedEvent event) {
        try {
            notificationService.createNotification(
                    event.getUserId(),
                    "CERTIFICATE_ISSUED",
                    "Certificate issued for: " + event.getCourseTitle(),
                    event.getEntityId()
            );
        } catch (Exception e) {
            log.error("Failed to create notification for certificate: {}", e.getMessage());
        }
    }
}
