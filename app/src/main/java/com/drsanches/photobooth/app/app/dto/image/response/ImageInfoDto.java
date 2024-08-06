package com.drsanches.photobooth.app.app.dto.image.response;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ImageInfoDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String path;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String thumbnailPath;

    @Schema(description = "null for system image", pattern = GregorianCalendarConvertor.PATTERN)
    private String created;

    @Schema(description = "null for system image")
    private String ownerId;
}
