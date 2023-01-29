package com.drsanches.photobooth.app.common.exception.application;

import com.drsanches.photobooth.app.common.exception.BaseException;

public class ApplicationException extends BaseException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Exception cause) {
        super(message, cause);
    }
}
