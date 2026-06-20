package com.rksdev.personallearningos.shared.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
public final class ErrorResponse {
    private String message;
    private HttpStatus status;
    private Instant timestamp;

    public ErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public ErrorResponse(String message, HttpStatus status, Instant timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

}
