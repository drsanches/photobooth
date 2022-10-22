package ru.drsanches.photobooth.exception.application;

public class UserAlreadyExistsException extends ApplicationException {

    private final static String FORMAT = "User '%s' already exists";

    public UserAlreadyExistsException(String user) {
        super(String.format(FORMAT, user));
    }

    public UserAlreadyExistsException(String user, Exception cause) {
        super(String.format(FORMAT, user), cause);
    }
}
