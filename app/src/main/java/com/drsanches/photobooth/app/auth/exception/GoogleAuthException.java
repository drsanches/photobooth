package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class GoogleAuthException extends BaseException {

    private static final String MESSAGE = "google.auth.error";

    public GoogleAuthException() {
        super(MESSAGE);
    }

    public GoogleAuthException(Exception cause) {
        super(MESSAGE, cause);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
