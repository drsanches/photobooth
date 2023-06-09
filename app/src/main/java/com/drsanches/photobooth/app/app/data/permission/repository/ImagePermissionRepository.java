package com.drsanches.photobooth.app.app.data.permission.repository;

import com.drsanches.photobooth.app.app.data.permission.model.ImagePermission;
import com.drsanches.photobooth.app.app.data.permission.model.ImagePermissionKey;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ImagePermissionRepository extends CrudRepository<ImagePermission, ImagePermissionKey> {

    Set<ImagePermission> findByIdUserId(String userId);
}
