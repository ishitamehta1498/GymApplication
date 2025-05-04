package com.email.notification_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                   WebRequest request) {
        log.error("RestExceptionHandler:handleMethodArgumentNotValidException");
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getAllErrors().stream()
                .map(error -> error.getDefaultMessage()).reduce("", (a, b) -> a + "\n" + b),
                request.getDescription(false));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleSQLIntegrityConstraintViolationException(DataIntegrityViolationException exception,
                                                                            WebRequest request) {
        log.error("RestExceptionHandler:handleDataIntegrityViolationException");
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception,
                                                                       WebRequest request) {
        log.error("RestExceptionHandler:handleMethodArgumentTypeMismatchException");
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleIllegalArgumentException(IllegalArgumentException exception,
                                                                       WebRequest request) {
        log.error("RestExceptionHandler:handleIllegalArgumentException");
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(MailSendException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMailSendException(MailSendException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleMailSendException");
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleRuntimeException(RuntimeException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleRuntimeException " + exception);
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                request.getDescription(false));
    }
}
