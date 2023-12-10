package com.drsanches.photobooth.app.common.token;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.common.token.data.TokenDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TokenService {

    private static final String TOKEN_TYPE = "Bearer";

    private static final int CALENDAR_FIELD = Calendar.DAY_OF_YEAR;

    private static final int EXPIRES_CALENDAR_VALUE = 10;

    private static final int REFRESH_EXPIRES_CALENDAR_VALUE = 100;

    @Autowired
    private TokenDomainService tokenDomainService;

    @Autowired
    private UserInfo userInfo;

    //TODO: Move expires to domain service
    public Token createToken(String userId, Role role) {
        GregorianCalendar expires = new GregorianCalendar();
        expires.add(CALENDAR_FIELD, EXPIRES_CALENDAR_VALUE);
        GregorianCalendar refreshExpires = new GregorianCalendar();
        refreshExpires.add(CALENDAR_FIELD, REFRESH_EXPIRES_CALENDAR_VALUE);
        Token savedToken = tokenDomainService.saveToken(userId, role, expires, refreshExpires);
        userInfo.init(userId, savedToken.getId(), savedToken.getRole());
        log.info("New token created. UserId: {}", userId);
        return savedToken;
    }

    public void validate(@Nullable String accessToken) {
        String accessTokenId = extractTokenId(accessToken)
                .orElseThrow(WrongTokenException::new);
        Token token = tokenDomainService.getValidTokenByAccessToken(accessTokenId);
        userInfo.init(token.getUserId(), token.getId(), token.getRole());
    }

    public Token refreshToken(@Nullable String refreshToken) {
        String refreshTokenId = extractTokenId(refreshToken)
                .orElseThrow(WrongTokenException::new);
        Token token = tokenDomainService.getValidTokenByRefreshToken(refreshTokenId);
        tokenDomainService.deleteById(token.getId());
        Token refreshedToken = createToken(token.getUserId(), token.getRole());
        log.info("Token refreshed. UserId: {}", refreshedToken.getUserId());
        return refreshedToken;
    }

    public void removeCurrentToken() {
        String tokenId = userInfo.getUserTokenId();
        tokenDomainService.deleteById(tokenId);
        userInfo.clean();
        log.info("Token deleted. UserId: {}", tokenId);
    }

    public void removeAllTokens(String userId) {
        List<Token> tokens = tokenDomainService.getTokensByUserId(userId);
        tokenDomainService.deleteAll(tokens);
        userInfo.clean();
        log.info("Tokens deleted. UserId: {}", userId);
    }

    private Optional<String> extractTokenId(@Nullable String token) {
        if (token == null) {
            return Optional.empty();
        }
        if (token.contains(TOKEN_TYPE + " ")) {
            return Optional.of(token.substring(TOKEN_TYPE.length() + 1));
        } else if (token.contains(TOKEN_TYPE + "%20")) {
            return Optional.of(token.substring(TOKEN_TYPE.length() + 3));
        }
        return Optional.empty();
    }
}
