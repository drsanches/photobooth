package com.drsanches.photobooth.app.auth.data.confirmation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {

    REGISTRATION(true),
    USERNAME_CHANGE(true),
    PASSWORD_CHANGE(true),
    EMAIL_CHANGE(true),
    DISABLE(true),

    GOOGLE_USERNAME_CHANGE(false);

    final boolean notificationsEnabled;
}
