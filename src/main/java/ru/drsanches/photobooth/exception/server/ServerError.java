package ru.drsanches.photobooth.exception.server;

import ru.drsanches.photobooth.exception.BaseException;

public class ServerError extends BaseException {

    private final static String message = "An internal error has occurred, try again later or contact support";

    public ServerError(String info) {
        super(message, info);
    }

    public ServerError(String info, Exception cause) {
        super(message, info, cause);
    }
}
