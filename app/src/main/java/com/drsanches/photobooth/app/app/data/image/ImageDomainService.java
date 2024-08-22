package com.drsanches.photobooth.app.app.data.image;

import com.drsanches.photobooth.app.app.data.image.repository.ImageRepository;
import com.drsanches.photobooth.app.app.exception.ImageNotFoundException;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.config.ImageConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageDomainService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageConverter imageConverter;

    public Image saveImage(byte[] imageData, String ownerId) {
        var savedImage = imageRepository.save(Image.builder()
                .id(UUID.randomUUID().toString())
                .data(imageData)
                .thumbnailData(imageConverter.toThumbnail(imageData))
                .ownerId(ownerId)
                .created(new GregorianCalendar())
                .build());
        log.debug("New image saved: {}", savedImage);
        return savedImage;
    }

    public Image saveSystemImage(String id, byte[] imageData) {
        var savedImage = imageRepository.save(Image.builder()
                .id(id)
                .data(imageData)
                .thumbnailData(imageConverter.toThumbnail(imageData))
                .ownerId(ImageConsts.SYSTEM_OWNER_ID)
                .created(new GregorianCalendar())
                .build());
        log.debug("New image saved: {}", savedImage);
        return savedImage;
    }

    public Image getImage(String imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException());
    }

    public List<Image> getImages(Collection<String> imageIds) {
        var images = imageRepository.findAllByIdInOrderByCreatedDesc(imageIds);
        if (images.size() != imageIds.size()) {
            var foundIds = images.stream()
                    .map(Image::getId)
                    .toList();
            log.warn("Images not found. Ids: {}", imageIds.stream()
                    .filter(x -> !foundIds.contains(x))
                    .toList());
        }
        return images;
    }

    public boolean exists(String imageId) {
        return imageRepository.existsById(imageId);
    }
}
