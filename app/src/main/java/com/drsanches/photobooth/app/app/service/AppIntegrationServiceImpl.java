package com.drsanches.photobooth.app.app.service;

import com.drsanches.photobooth.app.app.data.image.ImageDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import com.drsanches.photobooth.app.common.integration.app.AppIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppIntegrationServiceImpl implements AppIntegrationService {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private ImageDomainService imageDomainService;

    //TODO: Add transaction
    @Override
    public void safetyInitializeProfile(String userId, String username, String name, byte[] imageData) {
        try {
            if (userProfileDomainService.findEnabledById(userId).isEmpty()) {
                userProfileDomainService.create(userId, username);
            }
            if (name != null) {
                userProfileDomainService.updateProfileData(userId, name, null);
            }
            if (imageData != null) {
                var image = imageDomainService.saveImage(imageData, userId);
                userProfileDomainService.updateImageId(userId, image.getId());
            }
            log.debug("Profile initialized. UserId: {}", userId);
        } catch (Exception e) {
            log.error("Exception occurred during profile initialization. UserId: {}", userId, e);
        }
    }

    @Override
    public void createProfile(String userId, String username) {
        userProfileDomainService.create(userId, username);
    }

    @Override
    public void updateUsername(String userId, String username) {
        userProfileDomainService.updateUsername(userId, username);
    }

    @Override
    public void disable(String userId) {
        userProfileDomainService.disableUser(userId);
    }
}
