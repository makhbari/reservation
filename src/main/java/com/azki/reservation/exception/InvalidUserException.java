package com.azki.reservation.exception;

public class InvalidUserException extends BussinessException {
    public InvalidUserException(Integer code, String message, Throwable throwable) {
        super(code, message, throwable);
    }

    public InvalidUserException(Integer code, String message) {
        super(code, message);
    }
}
