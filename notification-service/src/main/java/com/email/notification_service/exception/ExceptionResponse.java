package com.email.notification_service.exception;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ExceptionResponse {
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String path;
}
