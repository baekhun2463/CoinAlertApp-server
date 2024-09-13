package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.coinalert.coinalertappserver.Model.Comment;
import org.coinalert.coinalertappserver.Model.CommentDTO;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Repository.CommentRepository;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/getComments/{postId}")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isPresent()) {
            List<Comment> comments = commentRepository.findByPost(postOptional.get());
            log.debug("Fetched Comments: {}", comments);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }else {
            log.error("Post not found for ID: {}", postId);
            return new ResponseEntity<>("댓글이 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/newComment")
    public ResponseEntity<?> savedComment(@RequestBody CommentDTO commentDTO) {

        Optional<Post> postOptional = postRepository.findById(commentDTO.getPostId());

        if (postOptional.isPresent()) {
            // Comment 객체 생성
            Comment comment = new Comment();
            comment.setPost(postOptional.get());
            comment.setAuthor(commentDTO.getAuthor());
            comment.setContent(commentDTO.getContent());
            comment.setLikes(commentDTO.getLikes());
            comment.setLiked(false);

            // Comment 저장
            commentRepository.save(comment);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("게시물을 찾지 못헀습니다.", HttpStatus.NOT_FOUND);
        }
    }


}
