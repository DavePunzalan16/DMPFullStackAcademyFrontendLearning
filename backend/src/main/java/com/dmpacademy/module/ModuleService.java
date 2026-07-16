package com.dmpacademy.module;

import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.course.Course;
import com.dmpacademy.course.CourseRepository;
import com.dmpacademy.module.dto.ModuleRequest;
import com.dmpacademy.module.dto.ModuleResponse;
import com.dmpacademy.module.dto.ReorderRequest;
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
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ModuleMapper moduleMapper;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public ModuleResponse addModule(UUID courseId, ModuleRequest request, UUID instructorId) {
        Course course = getCourseAndVerifyOwnership(courseId, instructorId);

        int nextOrder = moduleRepository.findMaxOrderIndexByCourseId(courseId) + 1;

        Module module = Module.builder()
                .course(course)
                .title(request.title().trim())
                .orderIndex(nextOrder)
                .build();

        Module saved = moduleRepository.save(module);
        log.info("Module added to course {}: {} at position {}", courseId, saved.getId(), nextOrder);
        return moduleMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public ModuleResponse updateModule(UUID moduleId, ModuleRequest request, UUID instructorId) {
        Module module = getModuleOrThrow(moduleId);
        verifyModuleOwnership(module, instructorId);

        module.setTitle(request.title().trim());
        Module saved = moduleRepository.save(module);
        return moduleMapper.toResponse(saved);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public void deleteModule(UUID moduleId, UUID instructorId) {
        Module module = getModuleOrThrow(moduleId);
        verifyModuleOwnership(module, instructorId);

        UUID courseId = module.getCourse().getId();
        int deletedIndex = module.getOrderIndex();

        // Delete the module (cascade will delete lessons)
        moduleRepository.delete(module);

        // Reorder remaining modules to fill the gap
        moduleRepository.decrementOrderIndexesAfter(courseId, deletedIndex);
        log.info("Module {} deleted from course {}, reordered remaining", moduleId, courseId);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public List<ModuleResponse> reorderModules(UUID courseId, ReorderRequest request, UUID instructorId) {
        getCourseAndVerifyOwnership(courseId, instructorId);

        List<Module> existingModules = moduleRepository.findByCourseIdOrderByOrderIndex(courseId);

        // Validate all IDs belong to this course
        if (request.moduleIds().size() != existingModules.size()) {
            throw new ValidationException("Module IDs count doesn't match existing modules in course");
        }

        for (UUID id : request.moduleIds()) {
            if (existingModules.stream().noneMatch(m -> m.getId().equals(id))) {
                throw new ValidationException("Module ID " + id + " does not belong to this course");
            }
        }

        // Apply new order
        for (int i = 0; i < request.moduleIds().size(); i++) {
            UUID moduleId = request.moduleIds().get(i);
            Module module = existingModules.stream()
                    .filter(m -> m.getId().equals(moduleId))
                    .findFirst()
                    .orElseThrow();
            module.setOrderIndex(i + 1);
        }

        moduleRepository.saveAll(existingModules);
        log.info("Modules reordered for course {}", courseId);

        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId).stream()
                .map(moduleMapper::toResponse)
                .toList();
    }

    public List<ModuleResponse> listModules(UUID courseId) {
        // Verify course exists
        courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId).stream()
                .map(moduleMapper::toResponse)
                .toList();
    }

    private Module getModuleOrThrow(UUID moduleId) {
        return moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));
    }

    private Course getCourseAndVerifyOwnership(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findByIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
        return course;
    }

    private void verifyModuleOwnership(Module module, UUID instructorId) {
        if (!module.getCourse().getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }
    }
}
