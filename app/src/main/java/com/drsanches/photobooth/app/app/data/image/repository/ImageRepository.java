package com.drsanches.photobooth.app.app.data.image.repository;

import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.common.aspects.MonitorTime;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@MonitorTime
public interface ImageRepository extends CrudRepository<Image, String> {

    @Override
    @NonNull
    <S extends Image> S save(@NonNull S entity);

    @Override
    @NonNull
    Optional<Image> findById(@NonNull String s);

    List<Image> findAllByIdInOrderByCreatedDesc(Collection<String> imageIds);

    @Override
    boolean existsById(@NonNull String id);
}
