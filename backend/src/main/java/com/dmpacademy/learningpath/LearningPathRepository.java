package com.dmpacademy.learningpath;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, UUID> {

    @Query("SELECT DISTINCT lp FROM LearningPath lp JOIN lp.courses lpc WHERE lpc.course.status = 'PUBLISHED'")
    Page<LearningPath> findAllWithPublishedCourses(Pageable pageable);

    @Query("SELECT lp FROM LearningPath lp JOIN FETCH lp.courses lpc JOIN FETCH lpc.course WHERE lp.id = :id")
    Optional<LearningPath> findByIdWithCourses(UUID id);
}
