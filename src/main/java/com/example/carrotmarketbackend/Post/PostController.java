package com.example.carrotmarketbackend.Post;

import com.example.carrotmarketbackend.Exception.S3ExceptionHandler;
import com.example.carrotmarketbackend.S3.S3Service;
import com.example.carrotmarketbackend.Enum.UserStatusEnum;
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
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<UserStatusEnum> post(@Valid @RequestBody PostDto dto) {
        return postService.save(dto);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> details(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(
            @PathVariable Long id,
            @Valid @RequestBody PostDto dto) {

        return postService.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Post> delete(@PathVariable Long id) {
        return  postService.delete(id);
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<String> getURL(@RequestParam String filename){
        String presignedUrl = s3Service.createPresignedUrlForUpload("test/" + filename);

        return ResponseEntity.ok(presignedUrl);
    }

    @ExceptionHandler(S3ExceptionHandler.class)
    public ResponseEntity<String> handleS3Exception(S3ExceptionHandler ex) {
        return ResponseEntity.status(ex.getStatusEnum().getStatusCode())
                .body(ex.getStatusEnum().getCode() + ": " + ex.getMessage());
    }

}
