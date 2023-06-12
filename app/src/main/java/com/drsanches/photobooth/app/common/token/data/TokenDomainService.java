package com.drsanches.photobooth.app.common.token.data;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.common.token.data.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TokenDomainService {

    private static final String TOKEN_TYPE = "Bearer";

    @Autowired
    private TokenRepository tokenRepository;

    public Token saveToken(String userId, Role role, GregorianCalendar expires) {
        Token savedToken = tokenRepository.save(Token.builder()
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .tokenType(TOKEN_TYPE)
                .expiresAt(expires)
                .userId(userId)
                .role(role)
                .build());
        log.debug("New token saved: {}", savedToken);
        return savedToken;
    }

    public Token getValidTokenByAccessToken(String accessToken) {
        return tokenRepository.findById(accessToken)
                .filter(it -> it.getExpiresAt().after(new GregorianCalendar()))
                .orElseThrow(WrongTokenException::new);
    }

    public List<Token> getTokensByUserId(String userId) {
        return tokenRepository.findByUserId(userId);
    }

    public Token getTokenByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(WrongTokenException::new);
    }

    public List<Token> getExpired() {
        return tokenRepository.findByExpiresAtLessThan(new GregorianCalendar());
    }

    public void deleteByAccessToken(String accessToken) {
        tokenRepository.deleteById(accessToken);
        log.debug("Token deleted. AccessToken: {}", accessToken);
    }

    public void deleteAll(List<Token> tokens) {
        tokenRepository.deleteAll(tokens);
        log.debug("Tokens deleted: {}", tokens);
    }
}
