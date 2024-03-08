package com.drsanches.photobooth.app.auth.service;

import com.drsanches.photobooth.app.auth.data.userauth.UserAuthDomainService;
import com.drsanches.photobooth.app.common.auth.AuthInfo;
import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.auth.data.token.model.Role;
import com.drsanches.photobooth.app.auth.data.token.model.Token;
import com.drsanches.photobooth.app.auth.data.token.TokenDomainService;
import com.drsanches.photobooth.app.common.integration.auth.AuthInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TokenService {

    private static final String TOKEN_TYPE = "Bearer";

    @Autowired
    private TokenDomainService tokenDomainService;

    @Autowired
    private UserAuthDomainService userAuthDomainService;

    //TODO: Remove?
    @Autowired
    private AuthInfo authInfo;

    public Token createToken(String userId, Role role) {
        var savedToken = tokenDomainService.createToken(userId, role);
        var user = userAuthDomainService.getEnabledById(savedToken.getUserId());
        authInfo.init(userId, user.getUsername(), savedToken.getId(), savedToken.getRole());
        log.info("New token created. UserId: {}", userId);
        return savedToken;
    }

    public AuthInfoDto validate(String accessToken) {
        var extractedAccessToken = extractToken(accessToken)
                .orElseThrow(WrongTokenException::new);
        var token = tokenDomainService.getValidTokenByAccessToken(extractedAccessToken);
        var user = userAuthDomainService.getEnabledById(token.getUserId());
        return new AuthInfoDto(
                token.getUserId(),
                user.getUsername(),
                token.getId(),
                token.getRole().toString()
        );
    }

    public Token refreshToken(@Nullable String refreshToken) {
        var extractedRefreshToken = extractToken(refreshToken)
                .orElseThrow(WrongTokenException::new);
        var token = tokenDomainService.getValidTokenByRefreshToken(extractedRefreshToken);
        tokenDomainService.deleteById(token.getId());
        var refreshedToken = createToken(token.getUserId(), token.getRole());
        log.info("Token refreshed. UserId: {}", refreshedToken.getUserId());
        return refreshedToken;
    }

    public void removeCurrentToken() {
        var tokenId = authInfo.getUserTokenId();
        tokenDomainService.deleteById(tokenId);
        authInfo.clean();
        log.info("Token deleted. UserId: {}", tokenId);
    }

    public void removeAllTokens(String userId) {
        var tokens = tokenDomainService.getTokensByUserId(userId);
        tokenDomainService.deleteAll(tokens);
        authInfo.clean();
        log.info("Tokens deleted. UserId: {}", userId);
    }

    private Optional<String> extractToken(String token) {
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
