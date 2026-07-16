package com.dmpacademy.gamification.dto;

public record XpResponse(
        int xpTotal,
        int currentLevel,
        int xpForNextLevel,
        boolean maxLevelReached
) {}
