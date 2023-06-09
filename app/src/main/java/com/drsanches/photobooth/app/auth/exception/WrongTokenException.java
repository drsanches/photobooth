package com.drsanches.photobooth.app.auth.exception;

public class WrongTokenException extends AuthException {

    public WrongTokenException() {
        super("Wrong token");
    }

    public WrongTokenException(Exception cause) {
        super("Wrong token", cause);
    }
}
