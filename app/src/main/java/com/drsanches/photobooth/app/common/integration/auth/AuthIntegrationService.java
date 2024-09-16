package com.drsanches.photobooth.app.common.integration.auth;

import java.util.Optional;

public interface AuthIntegrationService {

    UserCreationInfoDto createAccount(String username, String email, String password);

    Optional<AuthInfoDto> getAuthInfo(String token);
}
