package com.drsanches.photobooth.app.common.integration.auth;

public record UserInfoDto(
        String id,
        String username,
        String email
) {}
