package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.app.exception.ApplicationException;

public class EmailAlreadyExistsException extends ApplicationException { //TODO: AuthException

    private static final String MESSAGE = "Email already exists";

    public EmailAlreadyExistsException() {
        super(MESSAGE);
    }

    public EmailAlreadyExistsException(Exception cause) {
        super(MESSAGE, cause);
    }
}
