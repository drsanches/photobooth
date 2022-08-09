package ru.drsanches.photobooth.app.data.image.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.photobooth.app.data.image.model.Image;

import java.util.Collection;
import java.util.List;

public interface ImageRepository extends CrudRepository<Image, String> {

    List<Image> findAllByIdInOrderByCreatedTimeDesc(Collection<String> imageIds);
}
