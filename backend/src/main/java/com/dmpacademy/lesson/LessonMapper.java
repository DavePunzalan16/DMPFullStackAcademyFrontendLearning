package com.dmpacademy.lesson;

import com.dmpacademy.lesson.dto.LessonResponse;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getModule().getId(),
                lesson.getTitle(),
                lesson.getTextContent(),
                lesson.getVideoUrl(),
                lesson.getVideoId(),
                lesson.isPremium(),
                lesson.getOrderIndex(),
                lesson.getCreatedAt()
        );
    }
}
