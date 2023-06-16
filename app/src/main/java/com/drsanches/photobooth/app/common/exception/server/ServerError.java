package com.drsanches.photobooth.app.common.exception.server;

import com.drsanches.photobooth.app.common.exception.BaseException;

public class ServerError extends BaseException {

    private static final String MESSAGE = "An internal error has occurred, try again later or contact support";

    public ServerError(String info) {
        super(MESSAGE, info);
    }

    public ServerError(String info, Exception cause) {
        super(MESSAGE, info, cause);
    }

    private ServerError(String message, String info) {
        super(message, info);
    }

    public static ServerError createWithMessage(String message) {
        return new ServerError(message, message);
    }
}
