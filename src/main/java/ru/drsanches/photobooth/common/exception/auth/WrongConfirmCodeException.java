package ru.drsanches.photobooth.common.exception.auth;

public class WrongConfirmCodeException extends AuthException {

    private static final String DEFAULT_MESSAGE = "Wrong confirmation code";

    public WrongConfirmCodeException() {
        super(DEFAULT_MESSAGE);
    }

    public WrongConfirmCodeException(String message) {
        super(message);
    }

    public WrongConfirmCodeException(String message, Exception cause) {
        super(message, cause);
    }
}
