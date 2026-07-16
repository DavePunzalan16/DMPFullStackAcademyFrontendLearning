package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CourseCompletedEvent extends DomainEvent {

    public CourseCompletedEvent(UUID userId, UUID courseId) {
        super(userId, courseId, "COURSE_COMPLETED");
    }
}
