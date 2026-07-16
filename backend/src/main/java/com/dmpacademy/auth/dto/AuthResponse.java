package com.dmpacademy.auth.dto;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String email,
        String displayName,
        String role,
        int xpTotal,
        int level
) {}
