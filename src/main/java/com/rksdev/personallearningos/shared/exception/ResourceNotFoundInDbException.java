package com.rksdev.personallearningos.shared.exception;

public class ResourceNotFoundInDbException extends RuntimeException{

    public ResourceNotFoundInDbException(String message) {
        super(message);
    }
    public ResourceNotFoundInDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
