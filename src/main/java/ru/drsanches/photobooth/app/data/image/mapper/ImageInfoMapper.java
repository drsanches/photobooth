package ru.drsanches.photobooth.app.data.image.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.image.dto.response.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;
import ru.drsanches.photobooth.config.ImageConsts;

@Component
public class ImageInfoMapper {

    public ImageInfoDTO convert(Image image) {
        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId(image.getId());
        imageInfoDTO.setPath(ImageConsts.IMAGE_PATH_PREFIX + image.getId());
        imageInfoDTO.setThumbnailPath(ImageConsts.THUMBNAIL_PATH_PREFIX + image.getId());
        if (!image.getOwnerId().equals(ImageConsts.SYSTEM_OWNER_ID)) {
            imageInfoDTO.setCreatedTime(GregorianCalendarConvertor.convert(image.getCreatedTime()));
            imageInfoDTO.setOwnerId(image.getOwnerId());
        }
        return imageInfoDTO;
    }
}
