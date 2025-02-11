package com.azki.reservation.constant;

public enum ErrorsStatus {
    SUCCESS(0, "عملیات با موفقیت انجام شد"),
    UNKNOWN_ERROR(1, "خطای ناشناخته در سرور"),
    USER_ID_NOT_FOUND(2, "USER_ID_NOT_FOUND"),
    AVAILABLE_SLOT_NOT_FOUND(3, "AVAILABLE_SLOT_NOT_FOUND"),
    RESERVATION_NOT_FOUND(4, "RESERVATION_ID_NOT_FOUND");

    private final int code;
    private final String message;

    ErrorsStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
