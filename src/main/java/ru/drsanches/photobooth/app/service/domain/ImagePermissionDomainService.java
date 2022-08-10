package ru.drsanches.photobooth.app.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.drsanches.photobooth.app.data.image.model.ImagePermission;
import ru.drsanches.photobooth.app.data.image.repository.ImagePermissionRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImagePermissionDomainService {

    @Autowired
    private ImagePermissionRepository imagePermissionRepository;

    public void savePermissions(List<ImagePermission> imagePermissions) {
        imagePermissionRepository.saveAll(imagePermissions);
        log.info("New image permissions have been saved: {}", imagePermissions);
    }

    public Set<String> getImageIds(String userId) {
        return imagePermissionRepository.findByIdUserId(userId).stream()
                .map(ImagePermission::getImageId)
                .collect(Collectors.toSet());
    }
}
