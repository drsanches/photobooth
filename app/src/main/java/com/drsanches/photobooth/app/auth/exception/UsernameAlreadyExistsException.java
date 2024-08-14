package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.app.exception.ApplicationException;

public class UsernameAlreadyExistsException extends ApplicationException { //TODO: AuthException

    private static final String MESSAGE = "Username already exists";

    public UsernameAlreadyExistsException() {
        super(MESSAGE);
    }

    public UsernameAlreadyExistsException(Exception cause) {
        super(MESSAGE, cause);
    }
}
