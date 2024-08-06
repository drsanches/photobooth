package com.drsanches.photobooth.app.common.exception;

import com.drsanches.photobooth.app.app.exception.ApplicationException;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.app.exception.NoUsernameException;
import com.drsanches.photobooth.app.auth.exception.AuthException;
import com.drsanches.photobooth.app.auth.exception.GoogleLinkAuthException;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
//TODO: Split into different handlers for auth, app, etc
public class CustomExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> handleApplicationException(ApplicationException e) {
        log.warn("Application exception", e);
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        var path = e.getMessage().split("\\.");
        var message = e.getMessage().substring(path[0].length() + path[1].length() + 2);
        return handleApplicationException(new ApplicationException(message, e));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        log.warn("Auth exception", e);
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(GoogleLinkAuthException.class)
    public ResponseEntity<String> handleGoogleLinkAuthException(GoogleLinkAuthException e) {
        log.warn("Google link exception", e);
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoUserIdException.class, NoUsernameException.class})
    public ResponseEntity<String> handleNoUserException(ApplicationException e) {
        log.warn("No user exception", e);
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServerError.class)
    public ResponseEntity<String> handleServerError(ServerError e) {
        log.error("Server error", e);
        return new ResponseEntity<>(e.getMessage(), headers(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return handleServerError(new ServerError(e.getMessage(), e));
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
