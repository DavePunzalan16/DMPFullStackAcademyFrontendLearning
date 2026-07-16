package com.dmpacademy.learningpath;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.learningpath.dto.LearningPathRequest;
import com.dmpacademy.learningpath.dto.LearningPathResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning-paths")
@RequiredArgsConstructor
@Tag(name = "Learning Paths", description = "Curated course sequences")
public class LearningPathController {

    private final LearningPathService learningPathService;

    @PostMapping
    @Operation(summary = "Create a learning path (Admin)")
    public ResponseEntity<LearningPathResponse> create(@Valid @RequestBody LearningPathRequest request) {
        LearningPathResponse response = learningPathService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List learning paths (Public)")
    public ResponseEntity<PageResponse<LearningPathResponse>> list(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(learningPathService.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get learning path with courses")
    public ResponseEntity<LearningPathResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(learningPathService.getById(id));
    }
}
