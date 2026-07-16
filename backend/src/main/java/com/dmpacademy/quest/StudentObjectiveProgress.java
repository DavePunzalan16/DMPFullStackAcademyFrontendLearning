package com.dmpacademy.quest;

import com.dmpacademy.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "student_objective_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "objective_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentObjectiveProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objective_id", nullable = false)
    private QuestObjective objective;

    @Column(name = "current_count", nullable = false)
    @Builder.Default
    private int currentCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;
}
