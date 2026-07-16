package com.dmpacademy.lesson;

import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.common.util.YouTubeUrlParser;
import com.dmpacademy.lesson.dto.LessonCreateRequest;
import com.dmpacademy.lesson.dto.LessonResponse;
import com.dmpacademy.module.Module;
import com.dmpacademy.module.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonMapper lessonMapper;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public LessonResponse createLesson(UUID moduleId, LessonCreateRequest request, UUID instructorId) {
        Module module = getModuleAndVerifyOwnership(moduleId, instructorId);

        // Validate YouTube URL if provided
        String videoId = null;
        if (request.videoUrl() != null && !request.videoUrl().isBlank()) {
            videoId = YouTubeUrlParser.extractVideoId(request.videoUrl())
                    .orElseThrow(() -> new ValidationException(
                            "Invalid YouTube URL. Accepted formats: youtube.com/watch?v={id}, youtu.be/{id}, youtube.com/embed/{id}"));
        }

        int nextOrder = lessonRepository.findMaxOrderIndexByModuleId(moduleId) + 1;

        Lesson lesson = Lesson.builder()
                .module(module)
                .title(request.title().trim())
                .textContent(request.textContent())
                .videoUrl(request.videoUrl())
                .videoId(videoId)
                .isPremium(request.isPremium() != null && request.isPremium())
                .orderIndex(nextOrder)
                .build();

        Lesson saved = lessonRepository.save(lesson);
        log.info("Lesson created in module {}: {} at position {}", moduleId, saved.getId(), nextOrder);
        return lessonMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public LessonResponse updateLesson(UUID lessonId, LessonCreateRequest request, UUID instructorId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        verifyLessonOwnership(lesson, instructorId);

        // Validate YouTube URL if provided
        String videoId = null;
        if (request.videoUrl() != null && !request.videoUrl().isBlank()) {
            videoId = YouTubeUrlParser.extractVideoId(request.videoUrl())
                    .orElseThrow(() -> new ValidationException(
                            "Invalid YouTube URL. Accepted formats: youtube.com/watch?v={id}, youtu.be/{id}, youtube.com/embed/{id}"));
        }

        lesson.setTitle(request.title().trim());
        lesson.setTextContent(request.textContent());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setVideoId(videoId);
        lesson.setPremium(request.isPremium() != null && request.isPremium());

        Lesson saved = lessonRepository.save(lesson);
        return lessonMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public void deleteLesson(UUID lessonId, UUID instructorId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        verifyLessonOwnership(lesson, instructorId);

        UUID moduleId = lesson.getModule().getId();
        int deletedIndex = lesson.getOrderIndex();

        lessonRepository.delete(lesson);
        lessonRepository.decrementOrderIndexesAfter(moduleId, deletedIndex);
        log.info("Lesson {} deleted from module {}", lessonId, moduleId);
    }

    public LessonResponse getLesson(UUID lessonId) {
        Lesson lesson = getLessonOrThrow(lessonId);
        return lessonMapper.toResponse(lesson);
    }

    public List<LessonResponse> listLessonsByModule(UUID moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndex(moduleId).stream()
                .map(lessonMapper::toResponse)
                .toList();
    }

    private Lesson getLessonOrThrow(UUID lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));
    }

    private Module getModuleAndVerifyOwnership(UUID moduleId, UUID instructorId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));

        if (!module.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
        return module;
    }

    private void verifyLessonOwnership(Lesson lesson, UUID instructorId) {
        if (!lesson.getModule().getCourse().getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
    }
}
