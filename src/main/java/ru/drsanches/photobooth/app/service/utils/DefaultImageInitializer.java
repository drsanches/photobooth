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

    @Value("${application.image.initial-data.default}")
    private String defaultImage;

    @Value("${application.image.initial-data.no-photo}")
    private String noPhotoImage;

    @Value("${application.image.initial-data.deleted}")
    private String deletedImage;

    @Autowired
    private ImageDomainService imageDomainService;

    @Autowired
    private ImageConverter imageConverter;

    @Override
    public void initialize() {
        initialize(ImageInfoMapper.DEFAULT_AVATAR_ID, defaultImage);
        initialize(ImageInfoMapper.NO_PHOTO_IMAGE_ID, noPhotoImage);
        initialize(ImageInfoMapper.DELETED_AVATAR_ID, deletedImage);
    }

    private void initialize(String imageId, String imageData) {
        if (imageDomainService.exists(imageId)) {
            log.info("Image with id '{}' is already initialized", imageId);
            return;
        }
        byte[] image = Base64.getDecoder().decode(imageData);
        byte[] thumbnail = imageConverter.toThumbnail(image);
        imageDomainService.saveImage(new Image(imageId, image, thumbnail, new GregorianCalendar(), "system"));
        log.info("Image with id '{}' has been initialized", imageId);
    }
}
