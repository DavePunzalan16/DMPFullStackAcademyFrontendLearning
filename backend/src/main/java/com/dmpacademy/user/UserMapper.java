package com.dmpacademy.user;

import com.dmpacademy.auth.dto.AuthResponse;
import com.dmpacademy.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name(),
                user.getXpTotal(),
                user.getLevel()
        );
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole().name(),
                user.getAccountStatus().name(),
                user.getCreatedAt()
        );
    }
}
