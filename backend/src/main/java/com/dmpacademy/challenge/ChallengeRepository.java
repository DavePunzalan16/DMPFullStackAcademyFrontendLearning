package com.dmpacademy.challenge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<CodingChallenge, UUID> {

    @Query("SELECT c FROM CodingChallenge c JOIN FETCH c.testCases WHERE c.lesson.id = :lessonId")
    Optional<CodingChallenge> findByLessonIdWithTestCases(UUID lessonId);

    boolean existsByLessonId(UUID lessonId);
}
