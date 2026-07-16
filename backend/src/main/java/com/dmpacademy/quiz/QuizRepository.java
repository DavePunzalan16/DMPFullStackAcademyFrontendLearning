package com.dmpacademy.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    @Query("SELECT q FROM Quiz q JOIN FETCH q.questions qs JOIN FETCH qs.options WHERE q.lesson.id = :lessonId")
    Optional<Quiz> findByLessonIdWithQuestions(UUID lessonId);

    Optional<Quiz> findByLessonId(UUID lessonId);

    boolean existsByLessonId(UUID lessonId);
}
