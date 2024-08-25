package com.drsanches.photobooth.app.common.integration.notifier;

public interface NotifierIntegrationService {

    void updateEmail(String userId, String email);

    void removeEmail(String userId);
}
