package com.dmpacademy.module;

import com.dmpacademy.module.dto.ModuleRequest;
import com.dmpacademy.module.dto.ModuleResponse;
import com.dmpacademy.module.dto.ReorderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Module management within courses")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping("/api/v1/courses/{courseId}/modules")
    @Operation(summary = "Add a module to a course (Instructor, own course)")
    public ResponseEntity<ModuleResponse> addModule(
            @PathVariable UUID courseId,
            @Valid @RequestBody ModuleRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        ModuleResponse response = moduleService.addModule(courseId, request, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/courses/{courseId}/modules")
    @Operation(summary = "List modules for a course (ordered)")
    public ResponseEntity<List<ModuleResponse>> listModules(@PathVariable UUID courseId) {
        List<ModuleResponse> modules = moduleService.listModules(courseId);
        return ResponseEntity.ok(modules);
    }

    @PutMapping("/api/v1/modules/{id}")
    @Operation(summary = "Update a module title (Instructor, own course)")
    public ResponseEntity<ModuleResponse> updateModule(
            @PathVariable UUID id,
            @Valid @RequestBody ModuleRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        ModuleResponse response = moduleService.updateModule(id, request, instructorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/v1/courses/{courseId}/modules/reorder")
    @Operation(summary = "Reorder modules within a course (Instructor, own course)")
    public ResponseEntity<List<ModuleResponse>> reorderModules(
            @PathVariable UUID courseId,
            @Valid @RequestBody ReorderRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        List<ModuleResponse> modules = moduleService.reorderModules(courseId, request, instructorId);
        return ResponseEntity.ok(modules);
    }

    @DeleteMapping("/api/v1/modules/{id}")
    @Operation(summary = "Delete a module and its lessons (Instructor, own course)")
    public ResponseEntity<Void> deleteModule(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        moduleService.deleteModule(id, instructorId);
        return ResponseEntity.noContent().build();
    }
}
