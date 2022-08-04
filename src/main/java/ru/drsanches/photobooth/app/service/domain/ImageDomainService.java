package ru.drsanches.photobooth.app.service.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.data.image.repository.ImageRepository;
import ru.drsanches.photobooth.exception.application.NoImageException;
import java.util.Optional;

@Service
public class ImageDomainService {

    private final Logger LOG = LoggerFactory.getLogger(ImageDomainService.class);

    @Autowired
    private ImageRepository imageRepository;

    public void saveImage(Image image) {
        imageRepository.save(image);
        LOG.info("New image has been saved: {}", image);
    }

    public Image getImage(String imageId) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isEmpty()) {
            throw new NoImageException(imageId);
        }
        return image.get();
    }

    public boolean exists(String imageId) {
        return imageRepository.existsById(imageId);
    }
}