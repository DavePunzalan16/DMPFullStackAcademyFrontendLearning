package com.dmpacademy.notification;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.notification.dto.AnnouncementRequest;
import com.dmpacademy.notification.dto.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List my notifications")
    public ResponseEntity<PageResponse<NotificationResponse>> listNotifications(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        PageResponse<NotificationResponse> response = notificationService.listNotifications(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();
        notificationService.markAsRead(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/announcements")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create an announcement (Admin only)")
    public ResponseEntity<Void> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        notificationService.createAnnouncement(request.title());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
