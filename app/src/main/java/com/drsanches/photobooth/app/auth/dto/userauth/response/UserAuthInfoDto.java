package com.drsanches.photobooth.app.auth.dto.userauth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserAuthInfoDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
}
