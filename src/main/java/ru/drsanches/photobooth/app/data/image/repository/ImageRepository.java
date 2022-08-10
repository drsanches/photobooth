package ru.drsanches.photobooth.app.data.image.repository;

import org.springframework.data.repository.CrudRepository;
import ru.drsanches.photobooth.app.data.image.model.Image;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, String> {

    Optional<Image> findTopByIdInAndOwnerIdNotOrderByCreatedTimeDesc(Collection<String> imageIds, String orderId);

    List<Image> findAllByIdInOrderByCreatedTimeDesc(Collection<String> imageIds);
}
