package com.drsanches.photobooth.app.common.service;

public interface AuthIntegrationService {

    String getUsername(String userId);

    String getEmail(String userId);
}
