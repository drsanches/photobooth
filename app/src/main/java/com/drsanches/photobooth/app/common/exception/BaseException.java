package com.drsanches.photobooth.app.common.exception;

import com.drsanches.photobooth.app.common.exception.dto.ExceptionDto;
import com.drsanches.photobooth.app.common.exception.dto.ExceptionLogInfoDto;
import com.drsanches.photobooth.app.common.exception.dto.ExternalExceptionLogInfoDto;
import lombok.Getter;
import lombok.Setter;

import java.util.GregorianCalendar;
import java.util.UUID;

@Getter
@Setter
public class BaseException extends RuntimeException {

    private final String uuid = UUID.randomUUID().toString();

    private final GregorianCalendar timestamp = new GregorianCalendar();

    private String logMessage;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, String logMessage) {
        super(message);
        this.logMessage = logMessage;
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message, String logMessage, Throwable cause) {
        super(message, cause);
        this.logMessage = logMessage;
    }

    @Override
    public String getMessage() {
        return new ExceptionDto(uuid, super.getMessage()).toString();
    }
}

