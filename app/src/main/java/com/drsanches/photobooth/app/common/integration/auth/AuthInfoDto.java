package com.drsanches.photobooth.app.common.integration.auth;

public record AuthInfoDto(
        String userId,
        String username,
        String tokenId,
        String role
) {}
