package ru.drsanches.photobooth.app.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.image.mapper.ImageInfoMapper;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import ru.drsanches.photobooth.common.utils.Initializer;

import java.util.Base64;
import java.util.GregorianCalendar;

@Slf4j
@Component
public class DefaultImageInitializer implements Initializer {

    @Value("${application.default-image}")
    private String defaultImage;

    @Value("${application.no-photo-image}")
    private String noPhotoImage;

    @Autowired
    private ImageDomainService imageDomainService;

    @Override
    public void initialize() {
        initialize(ImageInfoMapper.DEFAULT_AVATAR_ID, defaultImage);
        initialize(ImageInfoMapper.NO_PHOTO_IMAGE_ID, noPhotoImage);
    }

    private void initialize(String imageId, String imageData) {
        if (imageDomainService.exists(imageId)) {
            log.info("Image with id '{}' is already initialized", imageId);
            return;
        }
        imageDomainService.saveImage(new Image(imageId, Base64.getDecoder().decode(imageData), new GregorianCalendar(), "system"));
        log.info("Image with id '{}' has been initialized", imageId);
    }
}
