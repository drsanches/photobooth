package com.drsanches.photobooth.app.common.service;

public interface NotifierIntegrationService {

    void setEmail(String userId, String email);

    void removeEmail(String userId);
}
