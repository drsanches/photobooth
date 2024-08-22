package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WrongUsernameAuthException extends BaseException {

    public static final String MESSAGE = "wrong.username";

    public WrongUsernameAuthException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
