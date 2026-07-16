package com.dmpacademy.notification;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.notification.dto.NotificationResponse;
import com.dmpacademy.user.AccountStatus;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNotification(UUID userId, String type, String title, UUID referenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .referenceId(referenceId)
                .build();

        notificationRepository.save(notification);
    }

    public PageResponse<NotificationResponse> listNotifications(UUID userId, Pageable pageable) {
        Page<NotificationResponse> page = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(n -> new NotificationResponse(n.getId(), n.getType(), n.getTitle(), n.getReferenceId(), n.isRead(), n.getCreatedAt()));
        return PageResponse.from(page);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You cannot access this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void createAnnouncement(String title) {
        List<User> activeUsers = userRepository.findAll().stream()
                .filter(u -> u.getAccountStatus() == AccountStatus.ACTIVE)
                .toList();

        for (User user : activeUsers) {
            Notification notification = Notification.builder()
                    .user(user)
                    .type("ANNOUNCEMENT")
                    .title(title)
                    .build();
            notificationRepository.save(notification);
        }
        log.info("Announcement created for {} users: {}", activeUsers.size(), title);
    }
}
