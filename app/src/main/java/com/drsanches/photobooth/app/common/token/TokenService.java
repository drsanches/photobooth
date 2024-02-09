package com.drsanches.photobooth.app.common.token;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.common.token.data.model.Role;
import com.drsanches.photobooth.app.common.token.data.model.Token;
import com.drsanches.photobooth.app.common.token.data.TokenDomainService;
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
    private UserInfo userInfo;

    public Token createToken(String userId, Role role) {
        var savedToken = tokenDomainService.createToken(userId, role);
        userInfo.init(userId, savedToken.getId(), savedToken.getRole());
        log.info("New token created. UserId: {}", userId);
        return savedToken;
    }

    public UserInfo validate(String accessToken) {
        var extractedAccessToken = extractToken(accessToken)
                .orElseThrow(WrongTokenException::new);
        var token = tokenDomainService.getValidTokenByAccessToken(extractedAccessToken);
        userInfo.init(token.getUserId(), token.getId(), token.getRole());
        return userInfo;
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
        var tokenId = userInfo.getUserTokenId();
        tokenDomainService.deleteById(tokenId);
        userInfo.clean();
        log.info("Token deleted. UserId: {}", tokenId);
    }

    public void removeAllTokens(String userId) {
        var tokens = tokenDomainService.getTokensByUserId(userId);
        tokenDomainService.deleteAll(tokens);
        userInfo.clean();
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
