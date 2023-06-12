package com.drsanches.photobooth.app.common.token;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.common.token.data.TokenDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final int CALENDAR_VALUE = 10;

    @Autowired
    private TokenDomainService tokenDomainService;

    @Autowired
    private TokenSupplier tokenSupplier;

    //TODO: Fix logging

    public Token createToken(String userId, Role role) {
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        Token savedToken = tokenDomainService.saveToken(userId, role, expiresAt);
        tokenSupplier.set(savedToken);
        log.info("New token created. UserId: {}", userId);
        return savedToken;
    }

    public void validate(String accessToken) {
        String accessTokenId = extractTokenId(accessToken)
                .orElseThrow(WrongTokenException::new);
        Token token = tokenDomainService.getValidTokenByAccessToken(accessTokenId);
        tokenSupplier.set(token);
    }

    public Token refreshToken(String refreshToken) {
        String refreshTokenId = extractTokenId(refreshToken)
                .orElseThrow(WrongTokenException::new);
        Token token = tokenDomainService.getTokenByRefreshToken(refreshTokenId);
        tokenDomainService.deleteByAccessToken(token.getAccessToken());
        Token refreshedToken = createToken(token.getUserId(), token.getRole());
        log.info("Token refreshed. UserId: {}", refreshedToken.getUserId());
        return refreshedToken;
    }

    public void removeCurrentToken() {
        Token token = tokenSupplier.get();
        tokenDomainService.deleteByAccessToken(token.getAccessToken());
        tokenSupplier.set(null);
        log.info("Token deleted. UserId: {}", token.getUserId());
    }

    public void removeAllTokens(String userId) {
        List<Token> tokens = tokenDomainService.getTokensByUserId(userId);
        tokenDomainService.deleteAll(tokens);
        tokenSupplier.set(null);
        log.info("Tokens deleted. UserId: {}", userId);
    }

    private Optional<String> extractTokenId(String token) {
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
