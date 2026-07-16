package com.dmpacademy.badge;

import com.dmpacademy.badge.dto.BadgeResponse;
import com.dmpacademy.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gamification/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Badge achievements")
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping
    @Operation(summary = "List earned badges for the current student")
    public ResponseEntity<PageResponse<BadgeResponse>> listBadges(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        PageResponse<BadgeResponse> response = badgeService.listEarnedBadges(studentId, pageable);
        return ResponseEntity.ok(response);
    }
}
