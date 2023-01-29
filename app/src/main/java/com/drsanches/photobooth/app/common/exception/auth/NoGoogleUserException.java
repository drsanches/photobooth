package com.drsanches.photobooth.app.common.exception.auth;

public class NoGoogleUserException extends AuthException {

    private final static String MESSAGE = "There is no user with this google account";

    public NoGoogleUserException() {
        super(MESSAGE);
    }

    public NoGoogleUserException(Exception cause) {
        super(MESSAGE, cause);
    }
}
