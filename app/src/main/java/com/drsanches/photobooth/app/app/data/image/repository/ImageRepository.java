package com.drsanches.photobooth.app.app.data.image.repository;

import com.drsanches.photobooth.app.app.data.image.model.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, String> {

    Optional<Image> findTopByIdInAndOwnerIdNotOrderByCreatedTimeDesc(Collection<String> imageIds, String orderId);

    List<Image> findAllByIdInOrderByCreatedTimeDesc(Collection<String> imageIds);
}
