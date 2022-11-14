package ru.drsanches.photobooth.exception.auth;

public class WrongConfirmCodeException extends AuthException {

    public WrongConfirmCodeException(String message) {
        super(message);
    }

    public WrongConfirmCodeException(String message, Exception cause) {
        super(message, cause);
    }
}
