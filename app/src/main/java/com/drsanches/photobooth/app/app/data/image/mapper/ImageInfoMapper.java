package com.drsanches.photobooth.app.app.data.image.mapper;

import com.drsanches.photobooth.app.app.data.image.dto.response.ImageInfoDto;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.stereotype.Component;

@Component
public class ImageInfoMapper {

    public ImageInfoDto convert(Image image) {
        ImageInfoDto imageInfoDto = ImageInfoDto.builder()
                .id(image.getId())
                .path(ImageConsts.IMAGE_PATH_PREFIX + image.getId())
                .thumbnailPath(ImageConsts.THUMBNAIL_PATH_PREFIX + image.getId())
                .build();
        if (image.getOwnerId().equals(ImageConsts.SYSTEM_OWNER_ID)) {
            return imageInfoDto;
        }
        return imageInfoDto.toBuilder()
                .createdTime(GregorianCalendarConvertor.convert(image.getCreatedTime()))
                .ownerId(image.getOwnerId())
                .build();
    }
}
