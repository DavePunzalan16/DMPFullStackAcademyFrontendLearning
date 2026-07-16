package com.dmpacademy.lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findByModuleIdOrderByOrderIndex(UUID moduleId);

    @Query("SELECT COALESCE(MAX(l.orderIndex), 0) FROM Lesson l WHERE l.module.id = :moduleId")
    int findMaxOrderIndexByModuleId(UUID moduleId);

    @Modifying
    @Query("UPDATE Lesson l SET l.orderIndex = l.orderIndex - 1 WHERE l.module.id = :moduleId AND l.orderIndex > :deletedIndex")
    void decrementOrderIndexesAfter(UUID moduleId, int deletedIndex);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.module.course.id = :courseId")
    long countByCourseId(UUID courseId);

    @Query("SELECT l.id FROM Lesson l WHERE l.module.course.id = :courseId ORDER BY l.module.orderIndex, l.orderIndex")
    List<UUID> findAllLessonIdsByCourseId(UUID courseId);
}
