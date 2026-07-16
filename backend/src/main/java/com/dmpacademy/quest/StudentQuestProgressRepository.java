package com.dmpacademy.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentQuestProgressRepository extends JpaRepository<StudentQuestProgress, UUID> {

    Optional<StudentQuestProgress> findByStudentIdAndQuestId(UUID studentId, UUID questId);

    List<StudentQuestProgress> findByStudentIdAndStatus(UUID studentId, String status);
}
