package com.dmpacademy.quest.dto;

import java.util.List;
import java.util.UUID;

public record QuestProgressResponse(
        UUID questId,
        String title,
        String status,
        int xpReward,
        List<ObjectiveProgressItem> objectives
) {
    public record ObjectiveProgressItem(
            UUID objectiveId, String description, String objectiveType,
            int targetCount, int currentCount, boolean completed
    ) {}
}
