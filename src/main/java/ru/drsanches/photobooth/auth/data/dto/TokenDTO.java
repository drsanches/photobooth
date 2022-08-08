package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

@Getter
@Setter
@ToString
public class TokenDTO {

    @ToString.Exclude
    @Schema(required = true)
    private String accessToken;

    @ToString.Exclude
    @Schema(required = true)
    private String refreshToken;

    @Schema(required = true)
    private String tokenType;

    @ToString.Exclude
    @Schema(required = true, description = GregorianCalendarConvertor.PATTERN)
    private String expiresAt;
}
