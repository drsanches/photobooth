package com.drsanches.photobooth.app.app.data.image.repository;

import com.drsanches.photobooth.app.app.data.image.model.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface ImageRepository extends CrudRepository<Image, String> {

    List<Image> findAllByIdInOrderByCreatedTimeDesc(Collection<String> imageIds);
}
