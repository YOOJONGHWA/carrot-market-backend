package com.example.carrotmarketbackend.S3;

import lombok.Getter;

@Getter
public class S3ExceptionHandler extends RuntimeException {

    private final S3statusEnum statusEnum;

    public S3ExceptionHandler(S3statusEnum statusEnum, Throwable cause) {
        super(cause);
        this.statusEnum = statusEnum;
    }

}
