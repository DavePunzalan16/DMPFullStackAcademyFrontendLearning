package com.dmpacademy.badge;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "badges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "icon_ref", nullable = false)
    private String iconRef;

    @Column(name = "criteria_type", nullable = false, length = 50)
    private String criteriaType;

    @Column(name = "criteria_value", nullable = false)
    private int criteriaValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = Instant.now(); }
}
