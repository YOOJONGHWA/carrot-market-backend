package com.example.carrotmarketbackend.Post;

import com.example.carrotmarketbackend.User.StatusEnum;
import com.example.carrotmarketbackend.User.User;
import com.example.carrotmarketbackend.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StatusEnum.USER_NOT_FOUND);
        }
    }

    public ResponseEntity<List<Post>> findAll() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }

    public ResponseEntity<Post> findById(Long id) {
        Optional<Post> post = postRepository.findById(id);

        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<Post> update(Long id, PostDto dto) {

        // 기존 게시물 조회
        Optional<Post> existingPostOptional = postRepository.findById(id);
        if (!existingPostOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 게시물 수정
        Post existingPost = existingPostOptional.get();
        log.info(String.valueOf(existingPost.getAuthor().getUsername()));
        log.info(dto.getAuthorId());
        // 작성자 검증
        Optional<User> userOptional = userRepository.findByUsername(dto.getAuthorUsername());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        System.out.println(userOptional);
        User user = userOptional.get();

        // 작성자와 현재 게시물 작성자가 동일한지 확인
        if (!existingPost.getAuthor().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 게시물 업데이트
        existingPost.setTitle(dto.getTitle());
        existingPost.setDescription(dto.getDescription());
        existingPost.setPrice(dto.getPrice());
        existingPost.setLatitude(dto.getLatitude());
        existingPost.setLongitude(dto.getLongitude());
        existingPost.setImage(dto.getImage());
        existingPost.setCreatedAt(LocalDateTime.now());

        postRepository.save(existingPost);

        return ResponseEntity.ok(existingPost);
    }

    public ResponseEntity<Post> delete(Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
