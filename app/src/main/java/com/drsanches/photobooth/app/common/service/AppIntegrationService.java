package com.drsanches.photobooth.app.common.service;

public interface AppIntegrationService {

    void updateUsername(String userId, String username);

    void disable(String userId);
}
