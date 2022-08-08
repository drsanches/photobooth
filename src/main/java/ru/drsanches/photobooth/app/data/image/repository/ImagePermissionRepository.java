package ru.drsanches.photobooth.app.data.image.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.photobooth.app.data.image.model.ImagePermission;
import ru.drsanches.photobooth.app.data.image.model.ImagePermissionKey;

import java.util.Set;

public interface ImagePermissionRepository extends CrudRepository<ImagePermission, ImagePermissionKey> {

    Set<ImagePermission> findByIdUserId(String userId);
}
