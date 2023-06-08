package com.drsanches.photobooth.app.app.data.image.mapper;

import com.drsanches.photobooth.app.app.data.image.dto.response.ImageInfoDto;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.stereotype.Component;

@Component
public class ImageInfoMapper {

    public ImageInfoDto convert(Image image) {
        ImageInfoDto imageInfoDto = new ImageInfoDto();
        imageInfoDto.setId(image.getId());
        imageInfoDto.setPath(ImageConsts.IMAGE_PATH_PREFIX + image.getId());
        imageInfoDto.setThumbnailPath(ImageConsts.THUMBNAIL_PATH_PREFIX + image.getId());
        if (!image.getOwnerId().equals(ImageConsts.SYSTEM_OWNER_ID)) {
            imageInfoDto.setCreatedTime(GregorianCalendarConvertor.convert(image.getCreatedTime()));
            imageInfoDto.setOwnerId(image.getOwnerId());
        }
        return imageInfoDto;
    }
}
