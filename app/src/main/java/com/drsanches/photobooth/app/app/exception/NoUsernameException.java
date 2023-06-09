package com.drsanches.photobooth.app.app.exception;

public class NoUsernameException extends ApplicationException {

    private final static String FORMAT = "There is no user with username '%s'";

    public NoUsernameException(String username) {
        super(String.format(FORMAT, username));
    }

    public NoUsernameException(String username, Exception cause) {
        super(String.format(FORMAT, username), cause);
    }
}
