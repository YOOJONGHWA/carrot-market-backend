package com.example.carrotmarketbackend.RefreshToken;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {

    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }


}