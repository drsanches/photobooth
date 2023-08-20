package com.drsanches.photobooth.app.app.dto.profile.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserInfoDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "null for deleted user")
    private String username;

    @Schema(description = "null for deleted user")
    private String name;

    @Schema(description = "null for deleted user")
    private String status;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String imagePath;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String thumbnailPath;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private RelationshipDto relationship;
}
