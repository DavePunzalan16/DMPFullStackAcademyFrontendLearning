package com.dmpacademy.quest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record QuestCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100) String title,

        @NotBlank(message = "Description is required")
        @Size(max = 500) String description,

        @Min(value = 1, message = "XP reward must be at least 1")
        @Max(value = 10000, message = "XP reward must not exceed 10000")
        int xpReward,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotEmpty(message = "At least one objective is required")
        @Size(max = 10, message = "Maximum 10 objectives")
        @Valid List<ObjectiveRequest> objectives
) {
    public record ObjectiveRequest(
            @NotBlank String objectiveType,
            @Min(1) int targetCount,
            @NotBlank @Size(max = 200) String description
    ) {}
}
