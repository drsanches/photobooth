package com.drsanches.photobooth.app.app.data.profile.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoDTO {

    @Schema(required = true)
    private String id;

    @Schema(description = "null for deleted user")
    private String username;

    @Schema(description = "null for deleted user")
    private String name;

    @Schema(description = "null for deleted user")
    private String status;

    @Schema(required = true, description = "null for deleted user")
    private String imagePath;

    @Schema(required = true)
    private String thumbnailPath;

    @Schema(required = true)
    private RelationshipDTO relationship;
}
