package com.dmpacademy.progress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, UUID> {

    boolean existsByStudentIdAndLessonId(UUID studentId, UUID lessonId);

    @Query("SELECT lp.lesson.id FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.lesson.id IN :lessonIds")
    List<UUID> findCompletedLessonIds(UUID studentId, List<UUID> lessonIds);

    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.lesson.module.course.id = :courseId")
    long countCompletedLessonsInCourse(UUID studentId, UUID courseId);

    @Query("SELECT lp FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.lesson.module.course.id = :courseId ORDER BY lp.completedAt DESC")
    List<LessonProgress> findByStudentAndCourse(UUID studentId, UUID courseId);
}
