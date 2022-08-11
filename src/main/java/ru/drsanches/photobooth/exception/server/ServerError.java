package ru.drsanches.photobooth.exception.server;

import ru.drsanches.photobooth.exception.dto.ExceptionDTO;

import java.util.UUID;

public class ServerError extends RuntimeException {

    private final static String message = "An internal error has occurred, try again later or contact support";

    private String info;

    private final String uuid = UUID.randomUUID().toString();

    public ServerError() {}

    public ServerError(String info) {
        this.info = info;
    }

    public ServerError(Exception cause) {
        super(cause);
    }

    public ServerError(String info, Exception cause) {
        super(cause);
        this.info = info;
    }

    public String getInfo() {
        return "{\"uuid\":\"" + uuid + "\",\"info\":\"" + info + "\"}";
    }

    @Override
    public String getMessage() {
        return new ExceptionDTO(message, uuid).toString();
    }
}
