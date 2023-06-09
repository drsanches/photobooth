package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;

public class AuthException extends BaseException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Exception cause) {
        super(message, cause);
    }
}
