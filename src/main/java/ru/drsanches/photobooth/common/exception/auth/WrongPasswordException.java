package ru.drsanches.photobooth.common.exception.auth;

public class WrongPasswordException extends AuthException {

    public WrongPasswordException() {
        super("Wrong password");
    }

    public WrongPasswordException(Exception cause) {
        super("Wrong password", cause);
    }
}
