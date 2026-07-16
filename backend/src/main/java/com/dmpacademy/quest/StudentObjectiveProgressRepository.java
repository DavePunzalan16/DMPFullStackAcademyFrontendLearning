package com.dmpacademy.quest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentObjectiveProgressRepository extends JpaRepository<StudentObjectiveProgress, UUID> {

    List<StudentObjectiveProgress> findByStudentIdAndObjectiveQuestId(UUID studentId, UUID questId);
}
