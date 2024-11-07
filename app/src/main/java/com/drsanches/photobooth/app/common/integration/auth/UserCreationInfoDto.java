package com.drsanches.photobooth.app.common.integration.auth;

public record UserCreationInfoDto(
        String id,
        String username,
        String email,
        String accessToken,
        String refreshToken
) {}
