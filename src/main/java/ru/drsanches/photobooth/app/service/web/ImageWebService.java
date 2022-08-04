package ru.drsanches.photobooth.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.common.token.TokenSupplier;
import ru.drsanches.photobooth.exception.application.ApplicationException;
import ru.drsanches.photobooth.exception.server.ServerError;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageWebService {

    private final Logger LOG = LoggerFactory.getLogger(ImageWebService.class);

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private ImageDomainService imageDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    public void uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) { //TODO: Add more validations
            throw new ApplicationException("File can not be empty");
        }
        String userId = tokenSupplier.get().getUserId();
        String imageId = UUID.randomUUID().toString();
        try {
            UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
            imageDomainService.saveImage(new Image(imageId, file.getBytes()));
            userProfile.setImageId(imageId);
            userProfileDomainService.save(userProfile);
            LOG.info("User with id '{}' updated his profile image, new image id is '{}'", userId, imageId);
        } catch (IOException e) {
            throw new ServerError(e);
        }
    }

    public byte[] getImage(String imageId) {
        return imageDomainService.getImage(imageId).getData();
    }

    public void deleteAvatar() {
        String userId = tokenSupplier.get().getUserId();
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        userProfile.setImageId(null);
        userProfileDomainService.save(userProfile);
        LOG.info("User with id '{}' deleted his profile image", userId);
    }
}