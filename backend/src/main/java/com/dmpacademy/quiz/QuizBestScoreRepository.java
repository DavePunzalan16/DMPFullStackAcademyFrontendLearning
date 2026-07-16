package com.dmpacademy.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizBestScoreRepository extends JpaRepository<QuizBestScore, UUID> {

    Optional<QuizBestScore> findByStudentIdAndQuizId(UUID studentId, UUID quizId);
}
