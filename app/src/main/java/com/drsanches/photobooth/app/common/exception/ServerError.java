package com.drsanches.photobooth.app.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServerError extends BaseException {

    private static final String MESSAGE = "unknown.error";

    private final String logMessage;

    public ServerError(String info) {
        super(MESSAGE);
        this.logMessage = info;
    }

    public ServerError(String info, Exception cause) {
        super(MESSAGE, cause);
        this.logMessage = info;
    }

    private ServerError(String message, String info) {
        super(message);
        this.logMessage = info;
    }

    public static ServerError createWithMessage(String message) {
        return new ServerError(message, message);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
