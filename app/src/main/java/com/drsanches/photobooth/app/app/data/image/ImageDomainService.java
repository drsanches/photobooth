package com.drsanches.photobooth.app.app.data.image;

import com.drsanches.photobooth.app.app.data.image.repository.ImageRepository;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.config.ImageConsts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ImageDomainService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageConverter imageConverter;

    public Image saveImage(byte[] imageData, String ownerId) {
        return save(UUID.randomUUID().toString(), imageData, ownerId);
    }

    public Image saveSystemImage(String id, byte[] imageData) {
        return save(id, imageData, ImageConsts.SYSTEM_OWNER_ID);
    }

    private Image save(String id, byte[] imageData, String ownerId) {
        var savedImage = imageRepository.save(Image.builder()
                .id(id)
                .data(imageData)
                .thumbnailData(imageConverter.toThumbnail(imageData))
                .ownerId(ownerId)
                .created(Instant.now())
                .build());
        log.info("New image saved: {}", savedImage);
        return savedImage;
    }

    public Optional<Image> findImage(String imageId) {
        return imageRepository.findById(imageId);
    }

    public List<Image> findAllImagesByIds(Collection<String> imageIds, Pageable pageable) {
        var images = imageRepository.findAllByIdInOrderByCreatedDesc(imageIds, pageable);
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
