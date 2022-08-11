package ru.drsanches.photobooth.exception.application;

import ru.drsanches.photobooth.exception.dto.ExceptionDTO;

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
        return new ExceptionDTO(message, uuid).toString();
    }
}
