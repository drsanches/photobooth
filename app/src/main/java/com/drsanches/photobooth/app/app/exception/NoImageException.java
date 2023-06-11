package com.drsanches.photobooth.app.app.exception;

public class NoImageException extends ApplicationException {

    private static final String FORMAT = "There is no image with id '%s'";

    public NoImageException(String imageId) {
        super(String.format(FORMAT, imageId));
    }

    public NoImageException(String imageId, Exception cause) {
        super(String.format(FORMAT, imageId), cause);
    }
}
