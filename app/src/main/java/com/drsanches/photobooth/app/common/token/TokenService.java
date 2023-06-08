package com.drsanches.photobooth.app.common.token;

import com.drsanches.photobooth.app.common.exception.auth.WrongTokenException;
import com.drsanches.photobooth.app.common.token.data.Role;
import com.drsanches.photobooth.app.common.token.data.Token;
import com.drsanches.photobooth.app.common.token.data.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.GregorianCalendar;
import java.util.UUID;

@Service
public class TokenService {

    private static final String TOKEN_TYPE = "Bearer";

    private static final int CALENDAR_FIELD = GregorianCalendar.DAY_OF_YEAR;

    private static final int CALENDAR_VALUE = 10;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenSupplier tokenSupplier;

    public Token createToken(String userId, Role role) {
        Token token = new Token();
        token.setAccessToken(UUID.randomUUID().toString());
        token.setRefreshToken(UUID.randomUUID().toString());
        token.setTokenType(TOKEN_TYPE);
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        token.setExpiresAt(expiresAt);
        token.setUserId(userId);
        token.setRole(role);
        tokenRepository.save(token);
        tokenSupplier.set(token);
        return token;
    }

    public void validate(String accessToken) {
        if (accessToken == null || extractTokenId(accessToken) == null) {
            throw new WrongTokenException();
        }
        Token token = tokenRepository.findById(extractTokenId(accessToken))
                .filter(it -> it.getExpiresAt().after(new GregorianCalendar()))
                .orElseThrow(() -> {
                    throw new WrongTokenException();
                });
        tokenSupplier.set(token);
    }

    public Token refreshToken(String refreshToken) {
        Token token = getTokenByRefreshToken(refreshToken);
        tokenRepository.deleteById(token.getAccessToken());
        return createToken(token.getUserId(), token.getRole());
    }

    public void removeCurrentToken() {
        tokenRepository.deleteById(tokenSupplier.get().getAccessToken());
        tokenSupplier.set(null);
    }

    public void removeAllTokens(String userId) {
        tokenRepository.deleteAll(tokenRepository.findByUserId(userId));
        tokenSupplier.set(null);
    }

    private Token getTokenByRefreshToken(String refreshToken) {
        if (refreshToken == null || extractTokenId(refreshToken) == null) {
            throw new WrongTokenException();
        }
        return tokenRepository.findByRefreshToken(extractTokenId(refreshToken))
                .orElseThrow(() -> {
                    throw new WrongTokenException();
                });
    }

    private String extractTokenId(String token) {
        if (token == null) {
            return null;
        }
        if (token.contains(TOKEN_TYPE + " ")) {
            return token.substring(TOKEN_TYPE.length() + 1);
        } else if (token.contains(TOKEN_TYPE + "%20")) {
            return token.substring(TOKEN_TYPE.length() + 3);
        }
        return null;
    }
}
