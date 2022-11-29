package ru.drsanches.photobooth.app.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.app.data.image.repository.ImageRepository;
import ru.drsanches.photobooth.app.service.utils.ImageConverter;
import ru.drsanches.photobooth.config.ImageConsts;
import ru.drsanches.photobooth.exception.application.NoImageException;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageDomainService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageConverter imageConverter;

    public Image saveImage(byte[] imageData, String ownerId) {
        Image image = new Image();
        image.setId(UUID.randomUUID().toString());
        image.setData(imageData);
        image.setThumbnailData(imageConverter.toThumbnail(imageData));
        image.setOwnerId(ownerId);
        image.setCreatedTime(new GregorianCalendar());
        Image savedImage = imageRepository.save(image);
        log.info("New image saved: {}", savedImage);
        return savedImage;
    }

    public Image saveSystemImage(String id, byte[] imageData) {
        Image image = new Image();
        image.setId(id);
        image.setData(imageData);
        image.setThumbnailData(imageConverter.toThumbnail(imageData));
        image.setOwnerId(ImageConsts.SYSTEM_OWNER_ID);
        image.setCreatedTime(new GregorianCalendar());
        Image savedImage = imageRepository.save(image);
        log.info("New image saved: {}", savedImage);
        return savedImage;
    }

    public Image getImage(String imageId) {
        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isEmpty()) {
            throw new NoImageException(imageId);
        }
        return image.get();
    }

    public Optional<Image> getLastImage(Collection<String> imageIds, String ownerId) {
        return imageRepository.findTopByIdInAndOwnerIdNotOrderByCreatedTimeDesc(imageIds, ownerId);
    }

    public List<Image> getImages(Collection<String> imageIds) {
        List<Image> images = imageRepository.findAllByIdInOrderByCreatedTimeDesc(imageIds);
        if (images.size() != imageIds.size()) {
            List<String> foundIds = images.stream()
                    .map(Image::getId)
                    .collect(Collectors.toList());
            log.warn("Images not found. Ids: {}", imageIds.stream()
                    .filter(x -> !foundIds.contains(x))
                    .collect(Collectors.toList()));
        }
        return images;
    }

    public boolean exists(String imageId) {
        return imageRepository.existsById(imageId);
    }
}
