package com.drsanches.photobooth.app.auth.dto.userauth.response;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
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

    @Schema(pattern = GregorianCalendarConvertor.PATTERN, requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private String expiresAt;
}
