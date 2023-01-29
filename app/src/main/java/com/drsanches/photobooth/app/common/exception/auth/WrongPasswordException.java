package com.drsanches.photobooth.app.common.exception.auth;

public class WrongPasswordException extends AuthException {

    public WrongPasswordException() {
        super("Wrong password");
    }

    public WrongPasswordException(Exception cause) {
        super("Wrong password", cause);
    }
}
