package com.azki.reservation.exception;

public class NotFoundException extends BussinessException{

    public NotFoundException(Integer code, String message) {
        super(code, message);
    }
}
