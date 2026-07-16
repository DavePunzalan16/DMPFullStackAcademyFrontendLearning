package com.dmpacademy.learningpath;

import com.dmpacademy.course.Course;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "learning_path_courses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"learning_path_id", "course_id"}),
        @UniqueConstraint(columnNames = {"learning_path_id", "order_index"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningPathCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learning_path_id", nullable = false)
    private LearningPath learningPath;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}
