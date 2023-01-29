package com.drsanches.photobooth.app.common.exception.auth;


import com.drsanches.photobooth.app.common.exception.application.ApplicationException;

//TODO: May be auth exception
public class NoGoogleUserException extends ApplicationException {

    private final static String MESSAGE = "There is no user with this google account";

    public NoGoogleUserException() {
        super(MESSAGE);
    }

    public NoGoogleUserException(Exception cause) {
        super(MESSAGE, cause);
    }
}
