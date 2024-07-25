package com.example.carrotmarketbackend.User;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<StatusEnum> save(UserDto dto) {

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .profileImage(dto.getProfileImage())
                .email(dto.getEmail())
                .bio(dto.getBio())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(StatusEnum.OK);

    }

    public ResponseEntity<UserDto> findAuth(CustomUser customUser) {
        if (customUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Long userId = customUser.getId();
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return ResponseEntity.ok(userDto);
    }
}
