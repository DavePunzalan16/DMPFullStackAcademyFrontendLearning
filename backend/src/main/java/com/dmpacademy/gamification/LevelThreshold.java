package com.dmpacademy.gamification;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "level_thresholds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LevelThreshold {

    @Id
    @Column(nullable = false)
    private int level;

    @Column(name = "xp_required", nullable = false, unique = true)
    private int xpRequired;
}
