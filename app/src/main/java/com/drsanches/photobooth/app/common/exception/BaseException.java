package com.drsanches.photobooth.app.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.GregorianCalendar;
import java.util.UUID;

@Getter
@Setter
public abstract class BaseException extends RuntimeException {

    private final String uuid = UUID.randomUUID().toString();

    private final GregorianCalendar timestamp = new GregorianCalendar();

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract HttpStatus getHttpCode();
}

