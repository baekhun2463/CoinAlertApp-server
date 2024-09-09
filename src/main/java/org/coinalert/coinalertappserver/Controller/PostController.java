package org.coinalert.coinalertappserver.Controller;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public PostController(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }


    @PostMapping("/newPost")
    public ResponseEntity<?> savedPost(@RequestBody Post post, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername())
                .or(() -> memberRepository.findByOauth2Id(Long.valueOf(userDetails.getUsername())));

        Member member  = memberOptional.get();
        post.setMember(member);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        post.setTimestamp(timestamp);

        Post savedPost = postRepository.save(post);
        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }

    @GetMapping("/getPosts")
    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        if(posts.isEmpty()) {
            return Collections.emptyList();
        }else {
            return posts;
        }
    }

    @PostMapping("/toggleLike")
    public ResponseEntity<Void> toggleLike(@RequestBody Map<String, Object> likeData) {
        Long postId = ((Number) likeData.get("postId")).longValue();
        Boolean isLiked = (Boolean) likeData.get("isLiked");

        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isPresent()) {
            Post post = postOptional.get();
            if(isLiked) {
                post.setLikes(post.getLikes() + 1);
            } else {
                post.setLikes(post.getLikes() - 1);
            }
            postRepository.save(post);
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}
