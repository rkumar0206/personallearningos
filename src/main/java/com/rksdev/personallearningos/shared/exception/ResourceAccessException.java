package com.rksdev.personallearningos.shared.exception;

public class ResourceAccessException extends RuntimeException {

    public ResourceAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceAccessException(String message) {
        super(message);
    }
}
