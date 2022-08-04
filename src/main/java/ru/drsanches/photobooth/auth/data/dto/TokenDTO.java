package ru.drsanches.photobooth.auth.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;

public class TokenDTO {

    @Schema(required = true)
    private String accessToken;

    @Schema(required = true)
    private String refreshToken;

    @Schema(required = true)
    private String tokenType;

    @Schema(required = true, description = GregorianCalendarConvertor.PATTERN)
    private String expiresAt;

    public TokenDTO() {}

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "TokenDTO{" +
                "tokenType='" + tokenType + '\'' +
                '}';
    }
}