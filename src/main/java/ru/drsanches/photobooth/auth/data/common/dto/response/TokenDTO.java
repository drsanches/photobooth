package ru.drsanches.photobooth.auth.data.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

@Getter
@Setter
@ToString
public class TokenDTO {

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
