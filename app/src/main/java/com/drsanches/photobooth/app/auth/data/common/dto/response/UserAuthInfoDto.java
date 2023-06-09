package com.drsanches.photobooth.app.auth.data.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserAuthInfoDto {

    @Schema(required = true)
    private String id;

    @Schema(required = true)
    private String username;

    @Schema
    private String email;
}
