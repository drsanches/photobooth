package ru.drsanches.photobooth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.drsanches.photobooth.exception.application.ApplicationException;
import ru.drsanches.photobooth.exception.application.NoUserIdException;
import ru.drsanches.photobooth.exception.application.NoUsernameException;
import ru.drsanches.photobooth.exception.auth.AuthException;
import ru.drsanches.photobooth.exception.server.ServerError;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class CustomExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> handleApplicationException(ApplicationException e) {
        LOG.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return handleApplicationException(new ApplicationException(e.getMessage(), e));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        LOG.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NoUserIdException.class, NoUsernameException.class})
    public ResponseEntity<String> handleNoUserException(ApplicationException e) {
        LOG.warn(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ServerError.class})
    public ResponseEntity<String> handleServerError(ServerError e) {
        LOG.error(e.getInfo(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return handleServerError(new ServerError(e.getMessage(), e));
    }
}
