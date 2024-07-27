package com.example.carrotmarketbackend.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(S3ExceptionWrapper.class)
    public ResponseEntity<String> handleS3Exception(S3ExceptionWrapper ex) {
        return ResponseEntity.status(ex.getStatusEnum().getStatusCode())
                .body(ex.getStatusEnum().getCode() + ": " + ex.getMessage());
    }

}
