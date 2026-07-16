package com.dmpacademy.certificate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);

    Page<Certificate> findByStudentIdOrderByIssuedAtDesc(UUID studentId, Pageable pageable);
}
