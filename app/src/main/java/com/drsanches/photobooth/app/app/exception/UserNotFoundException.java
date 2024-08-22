package com.drsanches.photobooth.app.app.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    private static final String MESSAGE = "user.not.found";

    public UserNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.NOT_FOUND;
    }
}
