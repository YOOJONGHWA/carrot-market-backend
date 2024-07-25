package com.example.carrotmarketbackend.User;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String profileImage;

    private String bio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
