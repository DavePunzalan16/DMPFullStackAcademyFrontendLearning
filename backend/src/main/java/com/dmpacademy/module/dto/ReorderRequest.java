package com.dmpacademy.module.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderRequest(
        @NotEmpty(message = "Module IDs list cannot be empty")
        List<UUID> moduleIds
) {}
