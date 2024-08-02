package com.drsanches.photobooth.app.common.integration.app;

public interface AppIntegrationService {

    void safetyInitializeProfile(String userId, String username, String name, byte[] avatar);

    void updateUsername(String userId, String username);

    void disable(String userId);
}
