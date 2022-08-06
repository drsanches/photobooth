package ru.drsanches.photobooth.app.data.image.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.image.dto.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

@Component
public class ImageInfoMapper {

    //TODO: Move to global consts?
    private static final String PATH = "/api/v1/image/";

    public ImageInfoDTO convert(Image image) {
        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId(image.getId());
        imageInfoDTO.setPath(PATH + image.getId());
        if (!image.getId().equals("default")) {
            imageInfoDTO.setCreatedTime(GregorianCalendarConvertor.convert(image.getCreatedTime()));
            imageInfoDTO.setOwnerId(image.getOwnerId());
        }
        return imageInfoDTO;
    }
}
