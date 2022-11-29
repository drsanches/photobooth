package ru.drsanches.photobooth.common.exception.auth;

import ru.drsanches.photobooth.common.exception.BaseException;

public class AuthException extends BaseException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Exception cause) {
        super(message, cause);
    }
}
