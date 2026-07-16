package com.dmpacademy.module;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Module, UUID> {

    List<Module> findByCourseIdOrderByOrderIndex(UUID courseId);

    @Query("SELECT COALESCE(MAX(m.orderIndex), 0) FROM Module m WHERE m.course.id = :courseId")
    int findMaxOrderIndexByCourseId(UUID courseId);

    long countByCourseId(UUID courseId);

    @Modifying
    @Query("UPDATE Module m SET m.orderIndex = m.orderIndex - 1 WHERE m.course.id = :courseId AND m.orderIndex > :deletedIndex")
    void decrementOrderIndexesAfter(UUID courseId, int deletedIndex);

    Optional<Module> findByIdAndCourseId(UUID id, UUID courseId);
}
