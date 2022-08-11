package ru.drsanches.photobooth.exception.auth;

import ru.drsanches.photobooth.exception.dto.ExceptionDTO;

import java.util.UUID;

public class AuthException extends RuntimeException {

    protected final String message;

    protected final String uuid = UUID.randomUUID().toString();

    public AuthException(String message) {
        this.message = message;
    }

    public AuthException(String message, Exception cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return new ExceptionDTO(message, uuid).toString();
    }
}
