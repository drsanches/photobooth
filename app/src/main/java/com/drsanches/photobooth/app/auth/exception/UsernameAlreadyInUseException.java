package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyInUseException extends BaseException {

    private static final String MESSAGE = "username.already.in.use";

    public UsernameAlreadyInUseException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
