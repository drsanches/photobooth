package ru.drsanches.photobooth.app.service.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.image.model.ImagePermission;
import ru.drsanches.photobooth.app.data.image.repository.ImagePermissionRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImagePermissionDomainService {

    private final Logger LOG = LoggerFactory.getLogger(ImagePermissionDomainService.class);

    @Autowired
    private ImagePermissionRepository imagePermissionRepository;

    public void savePermissions(List<ImagePermission> imagePermissions) {
        imagePermissionRepository.saveAll(imagePermissions);
        LOG.info("New image permissions have been saved: {}", imagePermissions);
    }

    public Set<String> getImageIds(String userId) {
        return imagePermissionRepository.findByIdUserId(userId).stream()
                .map(ImagePermission::getImageId)
                .collect(Collectors.toSet());
    }
}
