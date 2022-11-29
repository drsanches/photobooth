package ru.drsanches.photobooth.common.exception.application;

import ru.drsanches.photobooth.common.exception.BaseException;

public class ApplicationException extends BaseException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception cause) {
        super(message, cause);
    }
}
