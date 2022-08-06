package ru.drsanches.photobooth.app.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import ru.drsanches.photobooth.app.data.image.dto.UploadAvatarDTO;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.data.profile.model.UserProfile;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import ru.drsanches.photobooth.app.service.domain.UserProfileDomainService;
import ru.drsanches.photobooth.common.token.TokenSupplier;

import javax.validation.Valid;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Validated
public class ImageWebService {

    private final Logger LOG = LoggerFactory.getLogger(ImageWebService.class);

    @Autowired
    private UserProfileDomainService userProfileDomainService;

    @Autowired
    private ImageDomainService imageDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    public void uploadAvatar(@Valid UploadAvatarDTO uploadAvatarDTO) {
        String userId = tokenSupplier.get().getUserId();
        String imageId = UUID.randomUUID().toString();
        byte[] file = Base64.getDecoder().decode(uploadAvatarDTO.getFile());
        UserProfile userProfile = userProfileDomainService.getEnabledById(userId);
        imageDomainService.saveImage(new Image(imageId, file));
        userProfile.setImageId(imageId);
        userProfileDomainService.save(userProfile);
        LOG.info("User with id '{}' updated his profile image, new image id is '{}'", userId, imageId);
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