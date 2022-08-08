package ru.drsanches.photobooth.exception.application;

import java.util.UUID;

public class ApplicationException extends RuntimeException {

    protected final String message;

    protected final String uuid = UUID.randomUUID().toString();

    public ApplicationException(String message) {
        this.message = message;
    }

    public ApplicationException(String message, Exception cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "{\"uuid\":\"" + uuid + "\",\"message\":\"" + message + "\"}";
    }
}
