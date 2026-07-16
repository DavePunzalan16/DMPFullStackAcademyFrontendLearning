package com.dmpacademy.challenge;

import com.dmpacademy.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "challenge_completions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "challenge_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private CodingChallenge challenge;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt;

    @PrePersist
    protected void onCreate() {
        this.completedAt = Instant.now();
    }
}
