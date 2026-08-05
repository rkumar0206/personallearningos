package com.rksdev.personallearningos.shared.exception;

public class BadRequestBodyException extends RuntimeException {

    public BadRequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestBodyException(String message) {
        super(message);
    }
}
