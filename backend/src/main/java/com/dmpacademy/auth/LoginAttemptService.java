package com.dmpacademy.auth;

import com.dmpacademy.user.AccountStatus;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(30);

    private final UserRepository userRepository;

    public boolean isAccountLocked(User user) {
        return user.isLocked();
    }

    public Duration getRemainingLockoutDuration(User user) {
        if (user.getLockedUntil() == null) return Duration.ZERO;
        Duration remaining = Duration.between(Instant.now(), user.getLockedUntil());
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    @Transactional
    public void recordFailedAttempt(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plus(LOCKOUT_DURATION));
            user.setAccountStatus(AccountStatus.LOCKED);
            log.warn("Account locked for user {} after {} failed attempts", user.getEmail(), attempts);
        }

        userRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(User user) {
        if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            if (user.getAccountStatus() == AccountStatus.LOCKED) {
                user.setAccountStatus(AccountStatus.ACTIVE);
            }
            userRepository.save(user);
        }
    }
}
