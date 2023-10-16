package com.drsanches.photobooth.app.auth.data.confirmation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Operation {

    REGISTRATION,
    USERNAME_CHANGE,
    PASSWORD_CHANGE,
    EMAIL_CHANGE,
    DISABLE,
    GOOGLE_USERNAME_CHANGE
}
