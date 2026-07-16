package com.dmpacademy.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestRepository extends JpaRepository<Quest, UUID> {

    @Query("SELECT q FROM Quest q WHERE q.startDate <= :today AND q.endDate >= :today")
    List<Quest> findActiveQuests(LocalDate today);

    @Query("SELECT q FROM Quest q JOIN FETCH q.objectives WHERE q.id = :id")
    Optional<Quest> findByIdWithObjectives(UUID id);
}
