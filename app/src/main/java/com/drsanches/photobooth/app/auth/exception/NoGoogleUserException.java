package com.drsanches.photobooth.app.auth.exception;

public class NoGoogleUserException extends AuthException {

    private static final String MESSAGE = "There is no user with this google account";

    public NoGoogleUserException() {
        super(MESSAGE);
    }

    public NoGoogleUserException(Exception cause) {
        super(MESSAGE, cause);
    }
}
