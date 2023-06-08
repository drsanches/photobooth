package com.drsanches.photobooth.app.app.data.image.dto.response;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ImageInfoDto {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String path;

    @Schema(required = true)
    private String thumbnailPath;

    @Schema(description = GregorianCalendarConvertor.PATTERN)
    private String createdTime;

    @Schema
    private String ownerId;
}
