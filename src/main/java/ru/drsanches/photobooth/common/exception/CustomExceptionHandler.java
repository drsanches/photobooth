package ru.drsanches.photobooth.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.drsanches.photobooth.common.exception.application.ApplicationException;
import ru.drsanches.photobooth.common.exception.application.NoUserIdException;
import ru.drsanches.photobooth.common.exception.application.NoUsernameException;
import ru.drsanches.photobooth.common.exception.auth.AuthException;
import ru.drsanches.photobooth.common.exception.server.ServerError;
import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> handleApplicationException(ApplicationException e) {
        log.warn("Application exception: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return handleApplicationException(new ApplicationException(e.getMessage(), e));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        log.warn("Auth exception: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NoUserIdException.class, NoUsernameException.class})
    public ResponseEntity<String> handleNoUserException(ApplicationException e) {
        log.warn("No user exception: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ServerError.class})
    public ResponseEntity<String> handleServerError(ServerError e) {
        log.error("Server error: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return handleServerError(new ServerError(e.getMessage(), e));
    }
}
