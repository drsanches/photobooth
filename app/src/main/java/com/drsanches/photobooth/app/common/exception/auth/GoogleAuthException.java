package com.drsanches.photobooth.app.common.exception.auth;

public class GoogleAuthException extends AuthException {

    private static final String MESSAGE = "Google auth error";

    public GoogleAuthException() {
        super(MESSAGE);
    }

    public GoogleAuthException(Exception cause) {
        super(MESSAGE, cause);
    }
}
