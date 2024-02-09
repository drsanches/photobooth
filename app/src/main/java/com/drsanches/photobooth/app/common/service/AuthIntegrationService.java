package com.drsanches.photobooth.app.common.service;

import java.util.Optional;

public interface AuthIntegrationService {

    Optional<AuthInfoDto> getAuthInfo(String token);

    String getEmail(String userId);
}
