package com.dmpacademy.progress;

import com.dmpacademy.progress.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboards", description = "Student and admin dashboards")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @Operation(summary = "Get student dashboard (XP, streak, courses, activity)")
    public ResponseEntity<DashboardResponse> getStudentDashboard(Authentication authentication) {
        UUID studentId = (UUID) authentication.getPrincipal();
        DashboardResponse response = dashboardService.getStudentDashboard(studentId);
        return ResponseEntity.ok(response);
    }
}
