package com.dmpacademy.challenge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChallengeCompletionRepository extends JpaRepository<ChallengeCompletion, UUID> {

    boolean existsByStudentIdAndChallengeId(UUID studentId, UUID challengeId);
}
