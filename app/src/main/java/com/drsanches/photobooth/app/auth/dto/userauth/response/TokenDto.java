package com.drsanches.photobooth.app.auth.dto.userauth.response;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
public class TokenDto {

    @Schema(required = true)
    @ToString.Exclude
    private String accessToken;

    @Schema(required = true)
    @ToString.Exclude
    private String refreshToken;

    @Schema(required = true)
    private String tokenType;

    @Schema(required = true, description = GregorianCalendarConvertor.PATTERN)
    @ToString.Exclude
    private String expiresAt;
}
