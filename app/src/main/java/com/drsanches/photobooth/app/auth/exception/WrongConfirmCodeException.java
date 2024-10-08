package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WrongConfirmCodeException extends BaseException {

    private static final String MESSAGE = "wrong.confirmation.code";

    public WrongConfirmCodeException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
