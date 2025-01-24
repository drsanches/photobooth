package com.drsanches.photobooth.app.common.auth;

import com.drsanches.photobooth.app.auth.exception.WrongTokenAuthException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope
public class AuthInfo {

    private String userTokenId;

    private String userId;

    private String username;

    public void init(
            @NonNull String userId,
            @NonNull String username,
            @NonNull String userTokenId
    ) {
        this.userId = userId;
        this.username = username;
        this.userTokenId = userTokenId;
    }

    public void clean() {
        this.userTokenId = null;
        this.userId = null;
    }

    /**
     * Throws {@link WrongTokenAuthException} if the user is not authorized.
     */
    public String getUserTokenId() {
        if (userTokenId == null) {
            throw new WrongTokenAuthException();
        }
        return userTokenId;
    }

    /**
     * Throws {@link WrongTokenAuthException} if the user is not authorized.
     */
    public String getUserId() {
        if (userId == null) {
            throw new WrongTokenAuthException();
        }
        return userId;
    }

    public Optional<String> getUserIdOptional() {
        return Optional.ofNullable(userId);
    }

    /**
     * Throws {@link WrongTokenAuthException} if the user is not authorized.
     */
    public String getUsername() {
        if (username == null) {
            throw new WrongTokenAuthException();
        }
        return username;
    }

    public boolean isAuthorized() {
        return userTokenId != null && userId != null;
    }
}
