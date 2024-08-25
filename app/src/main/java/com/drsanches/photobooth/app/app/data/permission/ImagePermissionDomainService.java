package com.drsanches.photobooth.app.app.data.permission;

import com.drsanches.photobooth.app.app.data.permission.model.ImagePermission;
import com.drsanches.photobooth.app.app.data.permission.repository.ImagePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImagePermissionDomainService {

    @Autowired
    private ImagePermissionRepository imagePermissionRepository;

    public void saveAll(String imageId, List<String> userIds) {
        var imagePermissions = new ArrayList<ImagePermission>(userIds.size());
        userIds.forEach(userId -> imagePermissions.add(new ImagePermission(imageId, userId)));
        imagePermissionRepository.saveAll(imagePermissions);
        log.info("New image permissions saved: {}", imagePermissions);
    }

    public Set<String> findAllByImageId(String userId) {
        return imagePermissionRepository.findByIdUserId(userId).stream()
                .map(ImagePermission::getImageId)
                .collect(Collectors.toSet());
    }
}
