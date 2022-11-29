package ru.drsanches.photobooth.common.exception.auth;

import ru.drsanches.photobooth.common.exception.application.ApplicationException;

public class NoGoogleUserException extends ApplicationException {

    private final static String MESSAGE = "There is no user with this google account";

    public NoGoogleUserException() {
        super(MESSAGE);
    }

    public NoGoogleUserException(Exception cause) {
        super(MESSAGE, cause);
    }
}
