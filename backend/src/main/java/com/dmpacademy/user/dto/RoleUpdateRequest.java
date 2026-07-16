package com.dmpacademy.user.dto;

import com.dmpacademy.user.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "Role is required")
        Role role
) {}
