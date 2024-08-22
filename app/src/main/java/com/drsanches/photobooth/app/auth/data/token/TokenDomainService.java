package com.drsanches.photobooth.app.auth.data.token;

import com.drsanches.photobooth.app.auth.exception.WrongTokenAuthException;
import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.auth.data.token.model.Token;
import com.drsanches.photobooth.app.auth.data.token.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
        log.debug("New token saved: {}", savedToken);
        return savedToken;
    }

    public Token getValidTokenByAccessToken(String accessToken) {
        //TODO: Remove expired
        return tokenRepository.findByAccessToken(accessToken)
                .filter(it -> it.getExpires().after(new GregorianCalendar()))
                .orElseThrow(WrongTokenAuthException::new);
    }

    public Token getValidTokenByRefreshToken(String refreshToken) {
        //TODO: Remove expired
        return tokenRepository.findByRefreshToken(refreshToken)
                .filter(it -> it.getRefreshExpires().after(new GregorianCalendar()))
                .orElseThrow(WrongTokenAuthException::new);
    }

    public List<Token> getTokensByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    public List<Token> getExpired() {
        var now = new GregorianCalendar();
        return tokenRepository.findByExpiresLessThanAndRefreshExpiresLessThan(now, now);
    }

    public void deleteById(String tokenId) {
        tokenRepository.deleteById(tokenId);
        log.debug("Token deleted. Id: {}", tokenId);
    }

    public void deleteAll(List<Token> tokens) {
        tokenRepository.deleteAll(tokens);
        log.debug("Tokens deleted: {}", tokens);
    }
}
