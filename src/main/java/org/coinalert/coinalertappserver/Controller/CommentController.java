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
        log.debug("Received Comment DTO: {}", commentDTO);

        // CommentDTO의 모든 필드 값을 상세히 출력
        log.debug("CommentDTO Details - postId: {}, memberId: {}, content: {}, author: {}, likes: {}",
                commentDTO.getPostId(), commentDTO.getMemberId(), commentDTO.getContent(),
                commentDTO.getAuthor(), commentDTO.getLikes());

        if (commentDTO == null || commentDTO.getPostId() == null || commentDTO.getMemberId() == null) {
            log.error("Invalid Comment DTO: postId or memberId is null");
            return new ResponseEntity<>("Invalid Comment data", HttpStatus.BAD_REQUEST);
        }

        // Post와 Member 객체를 ID로 조회
        Optional<Post> postOptional = postRepository.findById(commentDTO.getPostId());
        Optional<Member> memberOptional = memberRepository.findById(commentDTO.getMemberId());

        // Post 조회 결과 로그 추가
        if (postOptional.isPresent()) {
            log.debug("Post found: {}", postOptional.get());
        } else {
            log.error("Post not found with ID: {}", commentDTO.getPostId());
        }

        // Member 조회 결과 로그 추가
        if (memberOptional.isPresent()) {
            log.debug("Member found: {}", memberOptional.get());
        } else {
            log.error("Member not found with ID: {}", commentDTO.getMemberId());
        }

        if (postOptional.isPresent() && memberOptional.isPresent()) {
            // Comment 객체 생성
            Comment comment = new Comment();
            comment.setPost(postOptional.get());
            comment.setMember(memberOptional.get());
            comment.setContent(commentDTO.getContent());
            comment.setLikes(commentDTO.getLikes());

            log.debug("Saving Comment: {}", comment);
            commentRepository.save(comment);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } else {
            log.error("Post or Member not found with given ID");
            return new ResponseEntity<>("Post or Member not found", HttpStatus.NOT_FOUND);
        }
    }


}
