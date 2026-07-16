package com.dmpacademy.gamification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface XpTransactionRepository extends JpaRepository<XpTransaction, UUID> {

    boolean existsByStudentIdAndSourceTypeAndSourceId(UUID studentId, String sourceType, UUID sourceId);
}
