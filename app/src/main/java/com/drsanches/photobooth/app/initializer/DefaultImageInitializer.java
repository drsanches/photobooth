package com.drsanches.photobooth.app.initializer;

import com.drsanches.photobooth.app.app.data.image.ImageDomainService;
import com.drsanches.photobooth.app.config.ImageConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
public class DefaultImageInitializer implements Initializer {

    @Value("${application.image.initial-data.default}")
    private String defaultImage;

    @Value("${application.image.initial-data.no-photo}")
    private String noPhotoImage;

    @Value("${application.image.initial-data.deleted}")
    private String deletedImage;

    @Autowired
    private ImageDomainService imageDomainService;

    @Override
    public void initialize() {
        initialize(ImageConsts.DEFAULT_AVATAR_ID, defaultImage);
        initialize(ImageConsts.NO_PHOTO_IMAGE_ID, noPhotoImage);
        initialize(ImageConsts.DELETED_AVATAR_ID, deletedImage);
    }

    private void initialize(String imageId, String imageData) {
        if (imageDomainService.exists(imageId)) {
            log.info("Image already initialized. Id: {}", imageId);
            return;
        }
        byte[] image = Base64.getDecoder().decode(imageData);
        imageDomainService.saveSystemImage(imageId, image);
        log.info("Image initialized. Id: {}", imageId);
    }
}
