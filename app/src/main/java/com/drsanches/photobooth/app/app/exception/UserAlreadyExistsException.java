package com.drsanches.photobooth.app.app.exception;

public class UserAlreadyExistsException extends ApplicationException {

    private final static String FORMAT = "User '%s' already exists";
    
    private final static String USERNAME_OR_EMAIL_FORMAT = "User with username='%s' or email='%s' already exists";

    public UserAlreadyExistsException(String user) {
        super(String.format(FORMAT, user));
    }

    public UserAlreadyExistsException(String user, Exception cause) {
        super(String.format(FORMAT, user), cause);
    }

    public UserAlreadyExistsException(String username, String email, Exception cause) {
        super(String.format(USERNAME_OR_EMAIL_FORMAT, username, email), cause);
    }
}
