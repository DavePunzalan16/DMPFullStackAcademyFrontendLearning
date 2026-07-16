package com.dmpacademy.quiz;

import com.dmpacademy.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quiz_best_scores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "quiz_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizBestScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "best_score", nullable = false)
    private int bestScore;

    @Column(nullable = false)
    private boolean passed;

    @Column(name = "first_passed_at")
    private Instant firstPassedAt;
}
