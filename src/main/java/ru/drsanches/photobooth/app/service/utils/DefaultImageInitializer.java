package ru.drsanches.photobooth.app.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import java.util.Base64;
import java.util.GregorianCalendar;

@Component
public class DefaultImageInitializer {

    private final Logger LOG = LoggerFactory.getLogger(DefaultImageInitializer.class);

    @Autowired
    private ImageDomainService imageDomainService;

    public void initialize() {
        if (imageDomainService.exists("default")) {
            LOG.info("Default image is already initialized");
            return;
        }
        //TODO: Move image to resources
        String DEFAULT_IMAGE = "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";
        imageDomainService.saveImage(new Image("default", Base64.getDecoder().decode(DEFAULT_IMAGE), new GregorianCalendar(), "system"));
        LOG.info("Default image has been initialized");
    }
}