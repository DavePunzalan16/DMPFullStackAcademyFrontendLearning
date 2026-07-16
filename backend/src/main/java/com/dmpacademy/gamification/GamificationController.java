package com.dmpacademy.gamification;

import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.gamification.dto.StreakResponse;
import com.dmpacademy.gamification.dto.XpResponse;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gamification")
@RequiredArgsConstructor
@Tag(name = "Gamification", description = "XP, levels, and streaks")
public class GamificationController {

    private final UserRepository userRepository;
    private final LevelThresholdRepository levelThresholdRepository;

    @GetMapping("/xp")
    @Operation(summary = "Get current XP, level, and progress to next level")
    public ResponseEntity<XpResponse> getXpInfo(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        int maxLevel = levelThresholdRepository.findMaxLevel();
        boolean maxReached = user.getLevel() >= maxLevel;

        int xpForNextLevel = 0;
        if (!maxReached) {
            xpForNextLevel = levelThresholdRepository.findNextLevel(user.getLevel())
                    .map(lt -> lt.getXpRequired() - user.getXpTotal())
                    .orElse(0);
        }

        XpResponse response = new XpResponse(
                user.getXpTotal(),
                user.getLevel(),
                Math.max(0, xpForNextLevel),
                maxReached
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/streak")
    @Operation(summary = "Get current streak info")
    public ResponseEntity<StreakResponse> getStreakInfo(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        boolean activityToday = today.equals(user.getLastActivityDate());

        // If last activity was before yesterday, streak is effectively 0
        // (will be reset on next activity)
        int displayStreak = user.getStreakCount();
        if (user.getLastActivityDate() != null
                && !activityToday
                && !user.getLastActivityDate().equals(today.minusDays(1))) {
            displayStreak = 0; // Streak broken but not yet updated in DB
        }

        StreakResponse response = new StreakResponse(
                displayStreak,
                user.getLongestStreak(),
                activityToday
        );

        return ResponseEntity.ok(response);
    }
}
