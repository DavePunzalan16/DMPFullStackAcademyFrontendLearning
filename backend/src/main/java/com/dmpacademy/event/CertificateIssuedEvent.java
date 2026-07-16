package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CertificateIssuedEvent extends DomainEvent {

    private final String courseTitle;

    public CertificateIssuedEvent(UUID userId, UUID certificateId, String courseTitle) {
        super(userId, certificateId, "CERTIFICATE_ISSUED");
        this.courseTitle = courseTitle;
    }
}
