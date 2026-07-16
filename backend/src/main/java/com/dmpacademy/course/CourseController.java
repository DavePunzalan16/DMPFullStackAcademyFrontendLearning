package com.dmpacademy.course;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.course.dto.CourseCreateRequest;
import com.dmpacademy.course.dto.CourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/courses")
    @Operation(summary = "Create a new course (Instructor only)")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        CourseResponse response = courseService.createCourse(request, instructorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/courses")
    @Operation(summary = "List published courses")
    public ResponseEntity<PageResponse<CourseResponse>> listPublishedCourses(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PageResponse<CourseResponse> response = courseService.listPublishedCourses(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Get course details")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable UUID id) {
        CourseResponse response = courseService.getCourseById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/courses/{id}")
    @Operation(summary = "Update a course (Instructor, own course only)")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable UUID id,
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        CourseResponse response = courseService.updateCourse(id, request, instructorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/courses/{id}/publish")
    @Operation(summary = "Publish a course (Instructor, own course only)")
    public ResponseEntity<CourseResponse> publishCourse(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        CourseResponse response = courseService.publishCourse(id, instructorId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/courses/{id}")
    @Operation(summary = "Soft-delete a course (Admin only)")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id) {
        courseService.softDeleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses/search")
    @Operation(summary = "Search and filter courses")
    public ResponseEntity<PageResponse<CourseResponse>> searchCourses(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Difficulty difficulty,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PageResponse<CourseResponse> response = courseService.searchCourses(query, categoryId, difficulty, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/instructor/courses")
    @Operation(summary = "List instructor's own courses")
    public ResponseEntity<PageResponse<CourseResponse>> listInstructorCourses(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID instructorId = (UUID) authentication.getPrincipal();
        PageResponse<CourseResponse> response = courseService.listInstructorCourses(instructorId, pageable);
        return ResponseEntity.ok(response);
    }
}
