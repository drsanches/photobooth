package ru.drsanches.photobooth.app.service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.Application;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.service.domain.ImageDomainService;
import ru.drsanches.photobooth.exception.server.ServerError;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@Component
public class DefaultImageInitializer {

    private final Logger LOG = LoggerFactory.getLogger(DefaultImageInitializer.class);

    @Autowired
    private ImageDomainService imageDomainService;

    public void initialize(String filename) {
        if (imageDomainService.exists("default")) {
            LOG.info("Default image is already initialized");
            return;
        }
        try {
            URL image = Application.class.getClassLoader().getResource(filename);
            if (image == null) {
                throw new ServerError("Default image not found");
            }
            imageDomainService.saveImage(new Image("default", new FileInputStream(new File(image.getPath())).readAllBytes()));
            LOG.info("Default image has been initialized");
        } catch (IOException e) {
            throw new ServerError("Default image loading error ", e);
        }
    }
}