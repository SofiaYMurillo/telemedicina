package com.healthtech.meditriage.auth;

import com.healthtech.meditriage.user.UserResponse;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        UserResponse user
) {
}
