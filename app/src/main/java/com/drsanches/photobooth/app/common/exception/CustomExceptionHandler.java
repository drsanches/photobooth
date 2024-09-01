package com.drsanches.photobooth.app.common.exception;

import com.drsanches.photobooth.app.common.exception.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException e) {
        var result = new ErrorResponseDto(UUID.randomUUID().toString(), "validation.error");
        e.getConstraintViolations().forEach(it -> {
            var message = it.getMessage();
            var field = StreamSupport.stream(it.getPropertyPath().spliterator(), false)
                    .skip(2)
                    .map(Object::toString)
                    .collect(Collectors.joining("."));
            result.addDetail(field, message);
        });
        log.warn("Validation error. Uuid: {}", result.getUuid(), e);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerError.class)
    public ResponseEntity<ErrorResponseDto> handleServerError(ServerError e) {
        log.error("Server error. Uuid: {}, info: {}", e.getUuid(), e.getLogMessage(), e);
        return new ResponseEntity<>(new ErrorResponseDto(e), e.getHttpCode());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(BaseException e) {
        log.warn("Base exception. Uuid: {}", e.getUuid(), e);
        return new ResponseEntity<>(new ErrorResponseDto(e), e.getHttpCode());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e) {
        return handleServerError(new ServerError(e.getMessage(), e));
    }
}
