package com.example.carrotmarketbackend.Exception;

import com.example.carrotmarketbackend.Enum.JwtEnum;
import lombok.Getter;

@Getter
public class JwtExceptionHandler extends RuntimeException {

    private final JwtEnum jwtEnum;

    public JwtExceptionHandler(JwtEnum statusEnum, Throwable cause) {
        super(cause);
        this.jwtEnum = statusEnum;
    }

}
