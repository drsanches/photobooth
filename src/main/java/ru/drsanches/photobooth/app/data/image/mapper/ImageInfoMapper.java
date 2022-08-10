package ru.drsanches.photobooth.app.data.image.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.controller.ImageController;
import ru.drsanches.photobooth.app.data.image.dto.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

@Component
public class ImageInfoMapper {

    public ImageInfoDTO convert(Image image) {
        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId(image.getId());
        imageInfoDTO.setPath(ImageController.IMAGE_PATH_PREFIX + image.getId());
        if (!image.getOwnerId().equals("system")) {
            imageInfoDTO.setCreatedTime(GregorianCalendarConvertor.convert(image.getCreatedTime()));
            imageInfoDTO.setOwnerId(image.getOwnerId());
        }
        return imageInfoDTO;
    }
}
