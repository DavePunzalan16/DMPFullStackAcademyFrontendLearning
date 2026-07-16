package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CourseEnrolledEvent extends DomainEvent {

    public CourseEnrolledEvent(UUID userId, UUID courseId) {
        super(userId, courseId, "COURSE_ENROLLED");
    }
}
