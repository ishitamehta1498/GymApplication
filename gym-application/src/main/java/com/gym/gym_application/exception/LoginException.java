package com.gym.gym_application.exception;

public class LoginException extends RuntimeException{
    public static final long serialVersionUID =1L;

    public LoginException(String message){
        super(message);
    }

}
