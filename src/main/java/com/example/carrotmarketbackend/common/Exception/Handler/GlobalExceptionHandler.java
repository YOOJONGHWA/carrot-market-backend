package com.example.carrotmarketbackend.common.Exception.Handler;

import com.example.carrotmarketbackend.common.Exception.Custom.UserExceptionHandler;
import com.example.carrotmarketbackend.common.Exception.JwtErrorCode;
import com.example.carrotmarketbackend.common.Exception.Custom.JwtExceptionHandler;
import com.example.carrotmarketbackend.common.Exception.UserErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

    // jwt 관련 예외 처리
    @ExceptionHandler(JwtExceptionHandler.class)
    public ResponseEntity<String> handleException(JwtExceptionHandler ex) {
        JwtErrorCode error = ex.getError();
        return ResponseEntity.status(error.getStatus()).body(error.getMessage());
    }

    // 회원 가입 관련 예외처리
    @ExceptionHandler(UserExceptionHandler.class)
    public ResponseEntity<String> handleException(UserExceptionHandler ex) {
        UserErrorCode error = ex.getError();
        return ResponseEntity.status(error.getStatus()).body(error.getMessage());
    }

    // 검증에 대한 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errorMessages = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return new ResponseEntity<>(String.join(", ", errorMessages), HttpStatus.BAD_REQUEST);
    }

}
