package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends BaseException {

    private static final String MESSAGE = "email.already.in.use";

    public EmailAlreadyInUseException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
