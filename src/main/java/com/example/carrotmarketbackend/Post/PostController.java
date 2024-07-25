package com.example.carrotmarketbackend.Post;

import com.example.carrotmarketbackend.User.StatusEnum;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<StatusEnum> post(@Valid @RequestBody PostDto dto) {

        return postService.save(dto);

    }

    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return postService.findAll();
    }

}
