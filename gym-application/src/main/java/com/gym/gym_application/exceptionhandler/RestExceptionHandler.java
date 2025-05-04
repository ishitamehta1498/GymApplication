package com.gym.gym_application.exceptionhandler;

import com.gym.gym_application.exception.*;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                   WebRequest request) {
        log.error("RestExceptionHandler : handleMethodArgumentNotValidException");
        return new ExceptionResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getAllErrors().stream()
                .map(error -> error.getDefaultMessage()).collect(Collectors.joining(", ")),
                request.getDescription(false));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleSQLIntegrityConstraintViolationException(DataIntegrityViolationException exception,
                                                                            WebRequest request) {
        log.error("RestExceptionHandler:handleDataIntegrityViolationException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleEntityNotFoundException(EntityNotFoundException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleEntityNotFoundException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception,
                                                                       WebRequest request) {
        log.error("RestExceptionHandler:handleMethodArgumentTypeMismatchException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleUserException(UserException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleUserException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(TraineeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleTraineeException(TraineeException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleTraineeException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(TrainerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleTrainerException(TrainerException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleTrainerException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(TrainingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleTrainingException(TrainerException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleTrainingException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(LoginException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleLoginException(LoginException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleLoginException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleFeignException(FeignException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleLoginException");
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleRuntimeException(RuntimeException exception, WebRequest request) {
        log.error("RestExceptionHandler:handleRuntimeException " + exception.toString());
        return new ExceptionResponse(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(),
                request.getDescription(false));
    }
}
