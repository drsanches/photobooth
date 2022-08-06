package ru.drsanches.photobooth.app.data.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

@Getter
@Setter
@ToString
public class ImageInfoDTO {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String path;

    @Schema(description = GregorianCalendarConvertor.PATTERN)
    private String createdTime;

    @Schema
    private String ownerId;
}
