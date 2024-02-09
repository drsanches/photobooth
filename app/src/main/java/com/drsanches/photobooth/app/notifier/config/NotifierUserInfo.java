package com.drsanches.photobooth.app.notifier.config;

import com.drsanches.photobooth.app.auth.exception.WrongTokenException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class NotifierUserInfo {

    private String userId;

    public void init(@NonNull String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        if (userId == null) {
            throw new WrongTokenException();
        }
        return userId;
    }
}
