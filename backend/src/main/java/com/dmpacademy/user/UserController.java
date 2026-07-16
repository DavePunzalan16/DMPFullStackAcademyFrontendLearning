package com.dmpacademy.user;

import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.user.dto.RoleUpdateRequest;
import com.dmpacademy.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile and admin user management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    @ApiResponse(responseCode = "200", description = "User profile returned")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        UserResponse response = userService.getCurrentUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all users (Admin only)")
    @ApiResponse(responseCode = "200", description = "Paginated user list")
    @ApiResponse(responseCode = "403", description = "Not an admin")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        PageResponse<UserResponse> response = userService.listUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Change a user's role (Admin only)")
    @ApiResponse(responseCode = "200", description = "Role updated")
    @ApiResponse(responseCode = "403", description = "Cannot change own role or not admin")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequest request,
            Authentication authentication
    ) {
        UUID adminId = (UUID) authentication.getPrincipal();
        UserResponse response = userService.updateUserRole(id, request, adminId);
        return ResponseEntity.ok(response);
    }
}
