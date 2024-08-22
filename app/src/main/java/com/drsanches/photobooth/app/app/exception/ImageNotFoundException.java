package com.drsanches.photobooth.app.app.exception;

import com.drsanches.photobooth.app.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ImageNotFoundException extends BaseException {

    private static final String MESSAGE = "image.not.found";

    public ImageNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus getHttpCode() {
        return HttpStatus.NOT_FOUND;
    }
}
