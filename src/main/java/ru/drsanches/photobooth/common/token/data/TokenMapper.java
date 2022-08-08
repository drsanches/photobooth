package ru.drsanches.photobooth.common.token.data;

import org.springframework.stereotype.Component;
import ru.drsanches.photobooth.common.utils.GregorianCalendarConvertor;
import ru.drsanches.photobooth.auth.data.dto.TokenDTO;

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
