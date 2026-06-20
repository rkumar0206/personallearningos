package com.rksdev.personallearningos.shared.exception.handler;

import com.rksdev.personallearningos.shared.exception.DuplicateResourceInDbException;
import com.rksdev.personallearningos.shared.exception.ResourceAccessException;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import com.rksdev.personallearningos.shared.model.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceInDbException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceInDbException(DuplicateResourceInDbException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ResourceNotFoundInDbException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundInDbException(ResourceNotFoundInDbException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessExceptionException(ResourceAccessException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.internalServerError().body(new ErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }


}
