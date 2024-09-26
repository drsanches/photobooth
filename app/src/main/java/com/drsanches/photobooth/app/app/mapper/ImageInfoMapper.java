package com.drsanches.photobooth.app.app.mapper;

import com.drsanches.photobooth.app.app.dto.image.common.GeolocationDto;
import com.drsanches.photobooth.app.app.dto.image.response.ImageInfoDto;
import com.drsanches.photobooth.app.app.data.image.model.Image;
import com.drsanches.photobooth.app.config.ImageConsts;
import org.springframework.stereotype.Component;

@Component
public class ImageInfoMapper {

    public ImageInfoDto convert(Image image) {
        var imageInfoDto = ImageInfoDto.builder()
                .id(image.getId())
                .path(ImageConsts.IMAGE_PATH.apply(image.getId()))
                .thumbnailPath(ImageConsts.THUMBNAIL_PATH.apply(image.getId()))
                .geo(image.getLat() != null && image.getLng() != null ?
                        new GeolocationDto(image.getLat(), image.getLng()) :
                        null)
                .build();
        if (image.getOwnerId().equals(ImageConsts.SYSTEM_OWNER_ID)) {
            return imageInfoDto;
        }
        return imageInfoDto.toBuilder()
                .created(image.getCreated().toString())
                .ownerId(image.getOwnerId())
                .build();
    }
}
