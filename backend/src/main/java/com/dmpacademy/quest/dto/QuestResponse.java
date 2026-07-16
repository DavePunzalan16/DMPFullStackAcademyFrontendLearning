package com.dmpacademy.quest.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record QuestResponse(
        UUID id,
        String title,
        String description,
        int xpReward,
        LocalDate startDate,
        LocalDate endDate,
        boolean active,
        List<ObjectiveResponse> objectives
) {
    public record ObjectiveResponse(
            UUID id, String objectiveType, int targetCount, String description, int orderIndex
    ) {}
}
