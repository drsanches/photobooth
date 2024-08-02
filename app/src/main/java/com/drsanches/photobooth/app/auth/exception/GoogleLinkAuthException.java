package com.drsanches.photobooth.app.auth.exception;

public class GoogleLinkAuthException extends AuthException {

    private static final String MESSAGE = "Can not link Google account";

    public GoogleLinkAuthException() {
        super(MESSAGE);
    }

    public GoogleLinkAuthException(Exception cause) {
        super(MESSAGE, cause);
    }
}
