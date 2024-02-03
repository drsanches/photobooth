package com.drsanches.photobooth.app.common.service;

import com.drsanches.photobooth.app.app.data.image.ImageDomainService;
import com.drsanches.photobooth.app.app.data.profile.UserProfileDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserProfileIntegrationService {

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private ImageDomainService imageDomainService;

    //TODO: Add transaction
    public void safetyInitializeProfile(String userId, String name, byte[] avatar) {
        try {
            if (name != null) {
                userProfileDomainService.updateProfileData(userId, name, null);
            }
            if (avatar != null) {
                var image = imageDomainService.saveImage(avatar, userId);
                userProfileDomainService.updateImageId(userId, image.getId());
            }
            log.debug("Profile initialized. UserId: {}", userId);
        } catch (Exception e) {
            log.error("Exception occurred during profile initialization. UserId: {}", userId, e);
        }
    }
}
