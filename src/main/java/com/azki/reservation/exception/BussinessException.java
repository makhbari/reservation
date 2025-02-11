package com.azki.reservation.exception;

import lombok.Getter;

public class BussinessException extends Exception {
    @Getter
    private Integer code;
    private String message;

    public BussinessException(Throwable throwable) {
        super(throwable);
    }

    public BussinessException(Integer code, String message, Throwable throwable) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public BussinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
