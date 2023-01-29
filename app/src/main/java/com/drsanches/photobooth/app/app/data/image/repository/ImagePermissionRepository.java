package com.drsanches.photobooth.app.app.data.image.repository;

import com.drsanches.photobooth.app.app.data.image.model.ImagePermission;
import com.drsanches.photobooth.app.app.data.image.model.ImagePermissionKey;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ImagePermissionRepository extends CrudRepository<ImagePermission, ImagePermissionKey> {

    Set<ImagePermission> findByIdUserId(String userId);
}
