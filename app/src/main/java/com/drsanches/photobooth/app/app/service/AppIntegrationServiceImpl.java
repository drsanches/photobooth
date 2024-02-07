package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.service.AppIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppIntegrationServiceImpl implements AppIntegrationService {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Override
    public void updateUsername(String userId, String username) {
        userProfileDomainService.updateUsername(userId, username);
    }

    @Override
    public void disable(String userId) {
        userProfileDomainService.disableUser(userId);
    }
}
