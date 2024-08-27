package com.drsanches.photobooth.app.app.dto.image.response;

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

    @Schema(description = "null for system image", pattern = "ISO-8601 (YYYY-MM-DDThh:mm:ss.sssZ)")
    private String created;

    @Schema(description = "null for system image")
    private String ownerId;
}
