package com.drsanches.photobooth.app.auth.mapper;

import com.drsanches.photobooth.app.auth.data.token.model.Token;
import com.drsanches.photobooth.app.auth.dto.userauth.response.TokenDto;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {

    public TokenDto convert(Token token) {
        return TokenDto.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .tokenType(token.getTokenType())
                .expires(token.getExpires().toString())
                .build();
    }
}
