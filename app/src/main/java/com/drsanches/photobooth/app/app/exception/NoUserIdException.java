package com.drsanches.photobooth.app.app.exception;

public class NoUserIdException extends ApplicationException {

    private static final String FORMAT = "There is no user with id '%s'";

    public NoUserIdException(String userId) {
        super(String.format(FORMAT, userId));
    }

    public NoUserIdException(String userId, Exception cause) {
        super(String.format(FORMAT, userId), cause);
    }
}
