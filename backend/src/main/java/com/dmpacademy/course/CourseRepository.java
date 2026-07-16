package com.dmpacademy.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' AND c.deleted = false")
    Page<Course> findPublishedCourses(Pageable pageable);

    Page<Course> findByInstructorIdAndDeletedFalse(UUID instructorId, Pageable pageable);

    Optional<Course> findByIdAndDeletedFalse(UUID id);

    @Query("SELECT c FROM Course c WHERE c.deleted = false")
    Page<Course> findAllNotDeleted(Pageable pageable);

    @Query("""
            SELECT c FROM Course c
            WHERE c.status = 'PUBLISHED' AND c.deleted = false
            AND (:query IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:categoryId IS NULL OR c.category.id = :categoryId)
            AND (:difficulty IS NULL OR c.difficulty = :difficulty)
            """)
    Page<Course> searchCourses(String query, UUID categoryId, Difficulty difficulty, Pageable pageable);
}
