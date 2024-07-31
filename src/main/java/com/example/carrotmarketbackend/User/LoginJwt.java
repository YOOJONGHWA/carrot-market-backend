package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.LoginResponseStatus;
import com.example.carrotmarketbackend.common.dto.ApiResponse;
import com.example.carrotmarketbackend.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


public class LoginJwt {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class LoginRequest {

        @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
        private String email;

        @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
        private String password;

    }
    @Getter
    @Setter
    @Builder
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;

        public static ApiResponse<TokenResponse> fromJwt(String accessToken, String refreshToken) {

            TokenResponse accessTokenResponse = TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ApiResponse.<TokenResponse>builder()
                    .status(LoginResponseStatus.LOGIN_SUCCESS.getStatusCode())
                    .message(LoginResponseStatus.LOGIN_SUCCESS.getMessage())
                    .body(accessTokenResponse)
                    .build();
        }
    }

}
