package com.dmpacademy.certificate;

import com.dmpacademy.course.Course;
import com.dmpacademy.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "certificates", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(name = "course_title", nullable = false, length = 150)
    private String courseTitle;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @PrePersist
    protected void onCreate() { this.issuedAt = Instant.now(); }
}
