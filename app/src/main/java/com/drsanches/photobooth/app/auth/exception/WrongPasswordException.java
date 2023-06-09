package com.drsanches.photobooth.app.auth.exception;

public class WrongPasswordException extends AuthException {

    public WrongPasswordException() {
        super("Wrong password");
    }

    public WrongPasswordException(Exception cause) {
        super("Wrong password", cause);
    }
}
