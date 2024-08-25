package com.drsanches.photobooth.app.auth.data.token;

import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.auth.data.token.model.Token;
import com.drsanches.photobooth.app.auth.data.token.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TokenDomainService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final int CALENDAR_FIELD = Calendar.DAY_OF_YEAR;
    private static final int EXPIRES_CALENDAR_VALUE = 10;
    private static final int REFRESH_EXPIRES_CALENDAR_VALUE = 100;

    @Autowired
    private TokenRepository tokenRepository;


    public Token createToken(String userId, Role role) {
        var expires = new GregorianCalendar();
        expires.add(CALENDAR_FIELD, EXPIRES_CALENDAR_VALUE);
        var refreshExpires = new GregorianCalendar();
        refreshExpires.add(CALENDAR_FIELD, REFRESH_EXPIRES_CALENDAR_VALUE);
        var savedToken = tokenRepository.save(Token.builder()
                .id(UUID.randomUUID().toString())
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .tokenType(TOKEN_TYPE)
                .expires(expires)
                .refreshExpires(refreshExpires)
                .userId(userId)
                .role(role)
                .build());
        log.info("New token saved: {}", savedToken);
        return savedToken;
    }

    public Optional<Token> findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    public Optional<Token> findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    public List<Token> findAllByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    public List<Token> findAllExpired() {
        var now = new GregorianCalendar();
        return tokenRepository.findByExpiresLessThanAndRefreshExpiresLessThan(now, now);
    }

    public void deleteById(String tokenId) {
        tokenRepository.deleteById(tokenId);
        log.info("Token deleted. Id: {}", tokenId);
    }

    public void deleteAll(List<Token> tokens) {
        tokenRepository.deleteAll(tokens);
        log.info("Tokens deleted: {}", tokens);
    }
}
