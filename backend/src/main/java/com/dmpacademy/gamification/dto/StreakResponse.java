package com.dmpacademy.gamification.dto;

public record StreakResponse(
        int currentStreak,
        int longestStreak,
        boolean activityCompletedToday
) {}
