package com.example.carrotmarketbackend.Exception;

import com.example.carrotmarketbackend.Enum.JwtEnum;
import lombok.Getter;

@Getter
public class JwtExceptionHandler extends RuntimeException {

    private final JwtEnum error;

    public JwtExceptionHandler(JwtEnum error) {
        super(error.getMessage());
        this.error = error;
    }

}