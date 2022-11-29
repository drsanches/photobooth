package ru.drsanches.photobooth.exception;

import lombok.Getter;
import lombok.Setter;
import ru.drsanches.photobooth.exception.dto.ExceptionDTO;
import ru.drsanches.photobooth.exception.dto.ExceptionLogInfoDTO;
import ru.drsanches.photobooth.exception.dto.ExternalExceptionLogInfoDTO;

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
        return new ExceptionDTO(uuid, super.getMessage()).toString();
    }

    public String log() {
        return new ExceptionLogInfoDTO(uuid, timestamp, logMessage, this).toString();
    }

    public static String log(Throwable e) {
        return new ExternalExceptionLogInfoDTO(e).toString();
    }
}

