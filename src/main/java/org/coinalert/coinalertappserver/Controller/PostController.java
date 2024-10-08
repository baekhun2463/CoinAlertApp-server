package org.coinalert.coinalertappserver.Controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;


    @PostMapping("/newPost")
    public ResponseEntity<?> savePost(@RequestBody Post post, @AuthenticationPrincipal UserDetails userDetails) {
        Post savedPost = postService.savePost(post, userDetails);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping("/getPosts")
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping("/toggleLike")
    public ResponseEntity<Void> toggleLike(@RequestBody Map<String, Object> likeData) {
        boolean success = postService.toggleLike(likeData);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
