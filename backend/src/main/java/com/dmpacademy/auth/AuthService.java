package com.dmpacademy.auth;

import com.dmpacademy.auth.dto.AuthResponse;
import com.dmpacademy.auth.dto.LoginRequest;
import com.dmpacademy.auth.dto.RegisterRequest;
import com.dmpacademy.common.exception.DuplicateResourceException;
import com.dmpacademy.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check email uniqueness (case-insensitive)
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new DuplicateResourceException("User", "email", request.email());
        }

        // Create user with STUDENT role, xp=0, level=1
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
}
