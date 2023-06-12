package com.drsanches.photobooth.app.auth.mapper;

import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
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
