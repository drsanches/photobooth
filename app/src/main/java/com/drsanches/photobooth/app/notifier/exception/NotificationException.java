package com.drsanches.photobooth.app.notifier.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;

public class NotificationException extends BaseException {

    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Exception cause) {
        super(message, cause);
    }
}
