package com.gym.gym_application.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String message;
    private String path;
}
