package com.dmpacademy.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class LessonCompletedEvent extends DomainEvent {

    private final UUID courseId;

    public LessonCompletedEvent(UUID userId, UUID lessonId, UUID courseId) {
        super(userId, lessonId, "LESSON_COMPLETED");
        this.courseId = courseId;
    }
}
