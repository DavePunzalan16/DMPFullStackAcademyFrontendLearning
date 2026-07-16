package com.dmpacademy.user;

import com.dmpacademy.auth.RefreshTokenRepository;
import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.user.dto.RoleUpdateRequest;
import com.dmpacademy.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> listUsers(Pageable pageable) {
        Page<UserResponse> page = userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
        return PageResponse.from(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse updateUserRole(UUID targetUserId, RoleUpdateRequest request, UUID adminUserId) {
        // Prevent admin from changing their own role
        if (targetUserId.equals(adminUserId)) {
            throw new AccessDeniedException("You cannot change your own role");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", targetUserId));

        // Prevent removing the last admin
        if (targetUser.getRole() == Role.ADMIN && request.role() != Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new ValidationException("Cannot remove the last admin. At least one admin must exist.");
            }
        }

        // Update role
        targetUser.setRole(request.role());
        User savedUser = userRepository.save(targetUser);

        // Revoke all refresh tokens for the user (forces re-login with new role)
        refreshTokenRepository.revokeAllByUserId(targetUserId);

        log.info("Admin {} changed role of user {} to {}", adminUserId, targetUserId, request.role());
        return userMapper.toUserResponse(savedUser);
    }
}
