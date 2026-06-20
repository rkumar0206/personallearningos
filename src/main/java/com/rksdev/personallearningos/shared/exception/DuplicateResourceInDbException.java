package com.rksdev.personallearningos.shared.exception;

public class DuplicateResourceInDbException extends RuntimeException {

    public DuplicateResourceInDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateResourceInDbException(String message) {
        super(message);
    }
}
