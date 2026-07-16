package com.dmpacademy.auth;

import com.dmpacademy.auth.dto.AuthResponse;
import com.dmpacademy.auth.dto.LoginRequest;
import com.dmpacademy.auth.dto.RegisterRequest;
import com.dmpacademy.common.exception.DuplicateResourceException;
import com.dmpacademy.config.JwtConfig;
import com.dmpacademy.user.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final LoginAttemptService loginAttemptService;
    private final CookieUtil cookieUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        User user = User.builder()
                .email(request.email().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.password()))
                .displayName(request.displayName().trim())
                .role(Role.STUDENT)
                .xpTotal(0)
                .level(1)
                .streakCount(0)
                .longestStreak(0)
                .accountStatus(AccountStatus.ACTIVE)
                .failedLoginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {} with role {}", savedUser.getEmail(), savedUser.getRole());

        return userMapper.toAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElse(null);

        // Generic error — don't reveal if email or password is wrong
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        // Check account lockout
        if (loginAttemptService.isAccountLocked(user)) {
            Duration remaining = loginAttemptService.getRemainingLockoutDuration(user);
            throw new AccountLockedException(remaining.toMinutes());
        }

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            loginAttemptService.recordFailedAttempt(user);
            throw new InvalidCredentialsException();
        }

        // Reset failed attempts on success
        loginAttemptService.resetFailedAttempts(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Store refresh token hash
        storeRefreshToken(user, refreshToken);

        // Set cookies
        cookieUtil.setAccessTokenCookie(response, accessToken);
        cookieUtil.setRefreshTokenCookie(response, refreshToken);

        log.info("User logged in: {}", user.getEmail());
        return userMapper.toAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue, HttpServletResponse response) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new InvalidCredentialsException();
        }

        // Validate the JWT itself
        if (!jwtService.validateToken(refreshTokenValue)) {
            throw new InvalidCredentialsException();
        }

        // Find the stored token by hash
        String tokenHash = hashToken(refreshTokenValue);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidCredentialsException::new);

        // Check if revoked (potential replay attack)
        if (storedToken.isRevoked()) {
            // Possible token reuse attack — revoke all tokens for this user
            log.warn("Refresh token replay detected for user {}", storedToken.getUser().getId());
            refreshTokenRepository.revokeAllByUserId(storedToken.getUser().getId());
            throw new InvalidCredentialsException();
        }

        // Check if expired
        if (storedToken.isExpired()) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new InvalidCredentialsException();
        }

        // Rotate: revoke old token
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Issue new token pair
        User user = storedToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        storeRefreshToken(user, newRefreshToken);

        cookieUtil.setAccessTokenCookie(response, newAccessToken);
        cookieUtil.setRefreshTokenCookie(response, newRefreshToken);

        return userMapper.toAuthResponse(user);
    }

    @Transactional
    public void logout(UUID userId, String refreshTokenValue, HttpServletResponse response) {
        // Revoke the specific refresh token if provided
        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            String tokenHash = hashToken(refreshTokenValue);
            refreshTokenRepository.findByTokenHash(tokenHash)
                    .ifPresent(token -> {
                        token.setRevoked(true);
                        refreshTokenRepository.save(token);
                    });
        }

        // Clear cookies
        cookieUtil.clearAuthCookies(response);
        log.info("User logged out: {}", userId);
    }

    private void storeRefreshToken(User user, String rawToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(Instant.now().plusMillis(jwtConfig.getRefreshTokenExpiryMs()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
