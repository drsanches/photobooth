package com.drsanches.photobooth.app.common.token.data;

import com.drsanches.photobooth.app.common.utils.GregorianCalendarConvertor;
import com.drsanches.photobooth.app.auth.data.common.dto.response.TokenDTO;
import org.springframework.stereotype.Component;

@Component
public class TokenMapper {

    public TokenDTO convert(Token token) {
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(token.getAccessToken());
        tokenDTO.setRefreshToken(token.getRefreshToken());
        tokenDTO.setTokenType(token.getTokenType());
        tokenDTO.setExpiresAt(GregorianCalendarConvertor.convert(token.getExpiresAt()));
        return tokenDTO;
    }
}
