package com.drsanches.photobooth.app.app.config;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;

@Component
@RequestScope
public class UserInfo {

    private String userId;

    private String username;

    public void init(@NonNull String userId, @NonNull String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        if (userId == null) {
            throw new WrongTokenException();
        }
        return userId;
    }

    public Optional<String> getUserIdOptional() {
        return Optional.ofNullable(userId);
    }

    public String getUsername() {
        if (username == null) {
            throw new WrongTokenException();
        }
        return username;
    }
}
