package com.drsanches.photobooth.app.auth.data.token;

import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.auth.data.token.model.Token;
import com.drsanches.photobooth.app.auth.data.token.repository.TokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TokenDomainService {

    private static final String TOKEN_TYPE = "Bearer";

    @Autowired
    private TokenRepository tokenRepository;


    public Token createToken(String userId, Role role) {
        var savedToken = tokenRepository.save(Token.builder()
                .id(UUID.randomUUID().toString())
                .accessToken(UUID.randomUUID().toString())
                .refreshToken(UUID.randomUUID().toString())
                .tokenType(TOKEN_TYPE)
                .expires(Instant.now().plus(10, ChronoUnit.DAYS))
                .refreshExpires(Instant.now().plus(100, ChronoUnit.DAYS))
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
        var now = Instant.now();
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
