package com.drsanches.photobooth.app.auth.config;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import com.drsanches.photobooth.app.auth.data.token.model.Role;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope
public class AuthInfo {

    private String userTokenId;

    private String userId;

    private Role role;

    public void init(@NonNull String userId, @NonNull String userTokenId, @NonNull Role role) {
        this.userId = userId;
        this.userTokenId = userTokenId;
        this.role = role;
    }

    public void clean() {
        this.userTokenId = null;
        this.userId = null;
        this.role = null;
    }

    /**
     * Throws {@link com.drsanches.photobooth.app.auth.exception.WrongTokenException} if the user is not authorized.
     */
    public String getUserTokenId() {
        if (userTokenId == null) {
            throw new WrongTokenException();
        }
        return userTokenId;
    }

    /**
     * Throws {@link com.drsanches.photobooth.app.auth.exception.WrongTokenException} if the user is not authorized.
     */
    public String getUserId() {
        if (userId == null) {
            throw new WrongTokenException();
        }
        return userId;
    }

    public Optional<String> getUserIdOptional() {
        return Optional.ofNullable(userId);
    }

    /**
     * Throws {@link com.drsanches.photobooth.app.auth.exception.WrongTokenException} if the user is not authorized.
     */
    public Role getRole() {
        if (role == null) {
            throw new WrongTokenException();
        }
        return role;
    }

    public boolean isAuthorized() {
        return userTokenId != null && userId != null && role != null;
    }
}
