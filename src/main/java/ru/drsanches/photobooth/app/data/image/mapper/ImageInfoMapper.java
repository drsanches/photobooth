package ru.drsanches.photobooth.app.data.image.mapper;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.app.data.image.dto.ImageInfoDTO;
import ru.drsanches.photobooth.app.data.image.model.Image;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

@Component
public class ImageInfoMapper {

    public static final String DEFAULT_AVATAR_ID = "default";

    public static final String DELETED_AVATAR_ID = "deleted";

    public static final String NO_PHOTO_IMAGE_ID = "no_photo";

    public static final String SYSTEM_OWNER_ID = "system";

    public static final String IMAGE_PATH_PREFIX = "/api/v1/image/";

    public ImageInfoDTO convert(Image image) {
        ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
        imageInfoDTO.setId(image.getId());
        imageInfoDTO.setPath(IMAGE_PATH_PREFIX + image.getId());
        if (!image.getOwnerId().equals(SYSTEM_OWNER_ID)) {
            imageInfoDTO.setCreatedTime(GregorianCalendarConvertor.convert(image.getCreatedTime()));
            imageInfoDTO.setOwnerId(image.getOwnerId());
        }
        return imageInfoDTO;
    }
}
