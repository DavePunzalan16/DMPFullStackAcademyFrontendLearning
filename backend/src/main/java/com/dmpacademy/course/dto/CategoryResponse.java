package com.dmpacademy.course.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name
) {}
