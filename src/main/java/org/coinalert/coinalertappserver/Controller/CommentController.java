package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.coinalert.coinalertappserver.Exception.CommentNotFoundException;
import org.coinalert.coinalertappserver.Exception.PostNotFoundException;
import org.coinalert.coinalertappserver.Model.Comment;
import org.coinalert.coinalertappserver.DTO.CommentDTO;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Repository.CommentRepository;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Repository.PostRepository;
import org.coinalert.coinalertappserver.Service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/getComments/{postId}")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping("/newComment")
    public ResponseEntity<?> savedComment(@RequestBody CommentDTO commentDTO) {
        try {
            Comment comment = commentService.saveComment(commentDTO);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        }catch (PostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/toggleLike")
    public ResponseEntity<Void> toggleLike(@RequestBody Map<String, Object> likeData) {
        Long commentId = ((Number) likeData.get("commentId")).longValue();
        Boolean isLiked = (Boolean) likeData.get("isLiked");

        try {
            commentService.toggleLike(commentId, isLiked);
            return ResponseEntity.ok().build();
        }catch(CommentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
