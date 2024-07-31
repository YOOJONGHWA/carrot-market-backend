package com.example.carrotmarketbackend.common.Exception.Custom;

import com.example.carrotmarketbackend.common.Exception.UserErrorCode;
import lombok.Getter;

@Getter
public class UserExceptionHandler extends RuntimeException{

    private final UserErrorCode error;

    public UserExceptionHandler(UserErrorCode error) {
        super(error.getMessage());
        this.error = error;
    }
}
