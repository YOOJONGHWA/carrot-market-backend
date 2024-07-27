package com.example.carrotmarketbackend.Exception;

import com.example.carrotmarketbackend.S3.S3statusEnum;
import lombok.Getter;

@Getter
public class S3ExceptionWrapper extends RuntimeException {

    private final S3statusEnum statusEnum;

    public S3ExceptionWrapper(S3statusEnum statusEnum, Throwable cause) {
        super(cause);
        this.statusEnum = statusEnum;
    }

}
