package com.dmpacademy.quest;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "quest_objectives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @Column(name = "objective_type", nullable = false, length = 50)
    private String objectiveType;

    @Column(name = "target_count", nullable = false)
    private int targetCount;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}
