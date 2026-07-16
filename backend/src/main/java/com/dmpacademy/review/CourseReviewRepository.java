package com.dmpacademy.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, UUID> {

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Page<CourseReview> findByCourseIdAndStatus(UUID courseId, String status, Pageable pageable);

    @Query("SELECT COALESCE(ROUND(AVG(r.rating), 1), 0) FROM CourseReview r WHERE r.course.id = :courseId AND r.status = 'APPROVED'")
    BigDecimal calculateAverageRating(UUID courseId);
}
