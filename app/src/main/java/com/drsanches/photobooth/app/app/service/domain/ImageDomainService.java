package com.drsanches.photobooth.app.app.service.domain;

import com.drsanches.photobooth.app.common.exception.application.NoImageException;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.app.data.image.repository.ImageRepository;
import com.drsanches.photobooth.app.app.service.utils.ImageConverter;
import com.drsanches.photobooth.app.config.ImageConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        log.debug("New image saved: {}", savedImage);
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
        log.debug("New image saved: {}", savedImage);
        return savedImage;
    }

    public Image getImage(String imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> {
            throw new NoImageException(imageId);
        });
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
