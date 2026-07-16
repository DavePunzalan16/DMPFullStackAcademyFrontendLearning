package com.dmpacademy.certificate;

import com.dmpacademy.certificate.dto.CertificateResponse;
import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.course.Course;
import com.dmpacademy.course.CourseRepository;
import com.dmpacademy.event.CertificateIssuedEvent;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void generateCertificate(UUID studentId, UUID courseId) {
        if (certificateRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.debug("Certificate already exists for student {} course {}", studentId, courseId);
            return;
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        Certificate cert = Certificate.builder()
                .student(student)
                .course(course)
                .studentName(student.getDisplayName())
                .courseTitle(course.getTitle())
                .build();

        Certificate saved = certificateRepository.save(cert);
        eventPublisher.publishEvent(new CertificateIssuedEvent(studentId, saved.getId(), course.getTitle()));
        log.info("Certificate issued: {} for student {} course {}", saved.getId(), studentId, courseId);
    }

    public PageResponse<CertificateResponse> listCertificates(UUID studentId, Pageable pageable) {
        Page<CertificateResponse> page = certificateRepository.findByStudentIdOrderByIssuedAtDesc(studentId, pageable)
                .map(c -> new CertificateResponse(c.getId(), c.getCourseTitle(), c.getStudentName(), c.getIssuedAt()));
        return PageResponse.from(page);
    }

    public CertificateResponse verifyCertificate(UUID certificateId) {
        Certificate cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", certificateId));
        return new CertificateResponse(cert.getId(), cert.getCourseTitle(), cert.getStudentName(), cert.getIssuedAt());
    }
}
