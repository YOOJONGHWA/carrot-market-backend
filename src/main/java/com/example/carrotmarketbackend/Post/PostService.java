package com.example.carrotmarketbackend.Post;

import com.example.carrotmarketbackend.User.StatusEnum;
import com.example.carrotmarketbackend.User.User;
import com.example.carrotmarketbackend.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor

public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<StatusEnum> save(PostDto dto) {

        // username을 기반으로 User 객체 조회
        Optional<User> user = userRepository.findByUsername(dto.getAuthorId());
        System.out.println(dto.getAuthorId());

        if (user.isPresent()) {
            // Post 객체 생성 및 저장
            Post post = Post.builder()
                    .title(dto.getTitle())
                    .description(dto.getDescription())
                    .price(dto.getPrice())
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .image(dto.getImage())
                    .createdAt(LocalDateTime.now())
                    .authorUsername(dto.getAuthorId())
                    .author(user.get()) // User 객체 설정
                    .build();
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(StatusEnum.OK);
        } else {
            // User가 없을 경우의 처리
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StatusEnum.USER_NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<List<Post>> findAll() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }
}
