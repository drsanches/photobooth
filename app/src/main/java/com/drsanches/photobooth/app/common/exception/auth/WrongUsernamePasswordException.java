package com.drsanches.photobooth.app.common.exception.auth;

public class WrongUsernamePasswordException extends AuthException {

    public WrongUsernamePasswordException() {
        super("Wrong username or password");
    }

    public WrongUsernamePasswordException(Exception cause) {
        super("Wrong username or password", cause);
    }
}
