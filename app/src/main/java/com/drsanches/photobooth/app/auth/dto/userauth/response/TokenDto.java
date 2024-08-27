package com.drsanches.photobooth.app.auth.dto.userauth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
public class TokenDto {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private String accessToken;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private String refreshToken;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenType;

    @Schema(pattern = "ISO-8601 (YYYY-MM-DDThh:mm:ss.sssZ)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private String expires;
}
