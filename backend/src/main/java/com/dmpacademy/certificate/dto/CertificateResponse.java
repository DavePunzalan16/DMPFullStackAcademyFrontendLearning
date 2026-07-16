package com.dmpacademy.certificate.dto;

import java.time.Instant;
import java.util.UUID;

public record CertificateResponse(
        UUID certificateId,
        String courseTitle,
        String studentName,
        Instant issuedAt
) {}
