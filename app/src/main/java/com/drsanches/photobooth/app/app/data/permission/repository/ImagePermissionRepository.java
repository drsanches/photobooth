package com.drsanches.photobooth.app.app.data.permission.repository;

import com.drsanches.photobooth.app.app.data.permission.model.ImagePermission;
import com.drsanches.photobooth.app.app.data.permission.model.ImagePermissionKey;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@MonitorTime
public interface ImagePermissionRepository extends CrudRepository<ImagePermission, ImagePermissionKey> {

    @Override
    @NonNull
    <S extends ImagePermission> Iterable<S> saveAll(@NonNull Iterable<S> entities);

    Set<ImagePermission> findByIdUserId(String userId);
}
