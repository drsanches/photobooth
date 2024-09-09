package com.drsanches.photobooth.app.auth.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {

    public static final String MESSAGE = "forbidden";

    public ForbiddenException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.FORBIDDEN;
    }
}
