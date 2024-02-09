package com.drsanches.photobooth.app.common.integration.auth;

import java.util.Optional;

public interface AuthIntegrationService {

    Optional<AuthInfoDto> getAuthInfo(String token);

    String getEmail(String userId);
}
