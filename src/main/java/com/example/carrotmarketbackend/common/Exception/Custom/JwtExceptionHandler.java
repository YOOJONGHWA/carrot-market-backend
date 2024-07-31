package com.example.carrotmarketbackend.common.Exception.Custom;

import com.example.carrotmarketbackend.common.Exception.JwtErrorCode;
import lombok.Getter;

@Getter
public class JwtExceptionHandler extends RuntimeException {

    private final JwtErrorCode error;

    public JwtExceptionHandler(JwtErrorCode error) {
        super(error.getMessage());
        this.error = error;
    }

}