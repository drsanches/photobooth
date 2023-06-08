package com.drsanches.photobooth.app.common.token.data;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDto;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {

    public TokenDto convert(Token token) {
        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(token.getAccessToken());
        tokenDto.setRefreshToken(token.getRefreshToken());
        tokenDto.setTokenType(token.getTokenType());
        tokenDto.setExpiresAt(GregorianCalendarConvertor.convert(token.getExpiresAt()));
        return tokenDto;
    }
}
