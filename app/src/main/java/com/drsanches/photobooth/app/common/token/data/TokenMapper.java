package com.drsanches.photobooth.app.common.token.data;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDto;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {

    public TokenDto convert(Token token) {
        return TokenDto.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .tokenType(token.getTokenType())
                .expiresAt(GregorianCalendarConvertor.convert(token.getExpiresAt()))
                .build();
    }
}
