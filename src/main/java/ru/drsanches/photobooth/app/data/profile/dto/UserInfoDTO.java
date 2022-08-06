package ru.drsanches.photobooth.app.data.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoDTO {

    @Schema(required = true)
    private String id;

    @Schema(description = "null for deleted user")
    private String username;

    @Schema(description = "null for deleted user")
    private String name;

    @Schema(description = "null for deleted user")
    private String status;

    @Schema(description = "null for deleted user")
    private String imagePath;
}
