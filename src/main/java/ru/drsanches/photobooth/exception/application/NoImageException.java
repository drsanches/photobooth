package ru.drsanches.photobooth.exception.application;

public class NoImageException extends ApplicationException {

    private final static String FORMAT = "There is no image with id '%s'";

    public NoImageException(String imageId) {
        super(String.format(FORMAT, imageId));
    }

    public NoImageException(String imageId, Exception cause) {
        super(String.format(FORMAT, imageId), cause);
    }
}