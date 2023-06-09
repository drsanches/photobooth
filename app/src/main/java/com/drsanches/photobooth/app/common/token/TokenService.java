package com.drsanches.photobooth.app.common.token;

import com.drsanches.photobooth.app.common.exception.auth.WrongTokenException;
import com.drsanches.photobooth.app.common.token.data.Role;
import com.drsanches.photobooth.app.common.token.data.Token;
import com.drsanches.photobooth.app.common.token.data.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
//TODO: TokenDomainService?
public class TokenService {

    private static final String TOKEN_TYPE = "Bearer";

    private static final int CALENDAR_FIELD = GregorianCalendar.DAY_OF_YEAR;

    private static final int CALENDAR_VALUE = 10;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenSupplier tokenSupplier;

    public Token createToken(String userId, Role role) {
        GregorianCalendar expiresAt = new GregorianCalendar();
        expiresAt.add(CALENDAR_FIELD, CALENDAR_VALUE);
        Token savedToken = tokenRepository.save(Token.builder()
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .tokenType(TOKEN_TYPE)
                .expiresAt(expiresAt)
                .userId(userId)
                .role(role)
                .build());
        log.debug("New token created: {}", savedToken);
        tokenSupplier.set(savedToken);
        return savedToken;
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
        log.debug("Token deleted: {}", token);
        return createToken(token.getUserId(), token.getRole());
    }

    public void removeCurrentToken() {
        Token token = tokenSupplier.get();
        tokenRepository.deleteById(token.getAccessToken());
        log.debug("Token deleted: {}", token);
        tokenSupplier.set(null);
    }

    public void removeAllTokens(String userId) {
        List<Token> tokens = tokenRepository.findByUserId(userId);
        tokenRepository.deleteAll(tokens);
        log.debug("Tokens deleted: {}", tokens);
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
