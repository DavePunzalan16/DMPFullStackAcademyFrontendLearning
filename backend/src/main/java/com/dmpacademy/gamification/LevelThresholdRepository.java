package com.dmpacademy.gamification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelThresholdRepository extends JpaRepository<LevelThreshold, Integer> {

    /**
     * Find the highest level whose xp_required is <= the given XP total.
     * This determines what level the student should be at.
     */
    @Query("SELECT lt FROM LevelThreshold lt WHERE lt.xpRequired <= :xpTotal ORDER BY lt.level DESC LIMIT 1")
    Optional<LevelThreshold> findHighestLevelForXp(int xpTotal);

    /**
     * Find the next level above the current level (for "XP to next level" calculation).
     */
    @Query("SELECT lt FROM LevelThreshold lt WHERE lt.level = :currentLevel + 1")
    Optional<LevelThreshold> findNextLevel(int currentLevel);

    @Query("SELECT MAX(lt.level) FROM LevelThreshold lt")
    int findMaxLevel();
}
