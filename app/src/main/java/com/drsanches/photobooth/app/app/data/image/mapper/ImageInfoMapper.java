package com.drsanches.photobooth.app.app.data.image.mapper;

import com.drsanches.photobooth.app.app.data.image.dto.response.ImageInfoDTO;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.stereotype.Component;

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
