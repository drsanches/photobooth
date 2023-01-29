package com.drsanches.photobooth.app.common.exception;

import com.drsanches.photobooth.app.common.exception.application.ApplicationException;
import com.drsanches.photobooth.app.common.exception.application.NoUserIdException;
import com.drsanches.photobooth.app.common.exception.application.NoUsernameException;
import com.drsanches.photobooth.app.common.exception.auth.AuthException;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({ApplicationException.class})
    public ResponseEntity<String> handleApplicationException(ApplicationException e) {
        log.warn("Application exception: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return handleApplicationException(new ApplicationException(e.getMessage(), e));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        log.warn("Auth exception: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NoUserIdException.class, NoUsernameException.class})
    public ResponseEntity<String> handleNoUserException(ApplicationException e) {
        log.warn("No user exception: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ServerError.class})
    public ResponseEntity<String> handleServerError(ServerError e) {
        log.error("Server error: {}", e.log());
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return handleServerError(new ServerError(e.getMessage(), e));
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
