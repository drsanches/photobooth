package com.drsanches.photobooth.app.app.dto.image.response;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
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