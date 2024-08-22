package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WrongTokenAuthException extends BaseException {

    public static final String MESSAGE = "wrong.token";

    public WrongTokenAuthException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
