package com.dmpacademy.badge;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {

    boolean existsByStudentIdAndBadgeId(UUID studentId, UUID badgeId);

    Page<UserBadge> findByStudentIdOrderByAwardedAtDesc(UUID studentId, Pageable pageable);
}
