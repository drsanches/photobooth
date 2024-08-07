package com.drsanches.photobooth.app.common.exception;

import com.drsanches.photobooth.app.app.exception.ApplicationException;
import com.drsanches.photobooth.app.app.exception.NoUserIdException;
import com.drsanches.photobooth.app.app.exception.NoUsernameException;
import com.drsanches.photobooth.app.auth.exception.AuthException;
import com.drsanches.photobooth.app.auth.exception.GoogleLinkAuthException;
import com.drsanches.photobooth.app.common.exception.dto.ExceptionDto;
import com.drsanches.photobooth.app.common.exception.server.ServerError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@ControllerAdvice
//TODO: Split into different handlers for auth, app, etc
public class CustomExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ExceptionDto> handleApplicationException(ApplicationException e) {
        log.warn("Application exception. Uuid: {}", e.getUuid(), e);
        return new ResponseEntity<>(new ExceptionDto(e), headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException e) {
        var result = new ExceptionDto(UUID.randomUUID().toString(), "validation.error");
        e.getConstraintViolations().forEach(it -> {
            var message = it.getMessage();
            var field = StreamSupport.stream(it.getPropertyPath().spliterator(), false)
                    .skip(2)
                    .map(Object::toString)
                    .collect(Collectors.joining("."));
            result.addDetail(field, message);
        });
        log.warn("Validation error. Uuid: {}", result.getUuid(), e);
        return new ResponseEntity<>(result, headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionDto> handleAuthException(AuthException e) {
        log.warn("Auth exception. Uuid: {}", e.getUuid(), e);
        return new ResponseEntity<>(new ExceptionDto(e), headers(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(GoogleLinkAuthException.class)
    public ResponseEntity<ExceptionDto> handleGoogleLinkAuthException(GoogleLinkAuthException e) {
        log.warn("Google link exception. Uuid: {}", e.getUuid(), e);
        return new ResponseEntity<>(new ExceptionDto(e), headers(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoUserIdException.class, NoUsernameException.class})
    public ResponseEntity<ExceptionDto> handleNoUserException(ApplicationException e) {
        log.warn("No user exception. Uuid: {}", e.getUuid(), e);
        return new ResponseEntity<>(new ExceptionDto(e), headers(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServerError.class)
    public ResponseEntity<ExceptionDto> handleServerError(ServerError e) {
        log.error("Server error. Uuid: {}, info: {}", e.getUuid(), e.getLogMessage(), e);
        return new ResponseEntity<>(new ExceptionDto(e), headers(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> handleRuntimeException(RuntimeException e) {
        return handleServerError(new ServerError(e.getMessage(), e));
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
