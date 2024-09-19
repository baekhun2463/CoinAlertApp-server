package org.coinalert.coinalertappserver.Service;

import lombok.RequiredArgsConstructor;
import org.coinalert.coinalertappserver.DTO.CommentDTO;
import org.coinalert.coinalertappserver.Exception.CommentNotFoundException;
import org.coinalert.coinalertappserver.Exception.PostNotFoundException;
import org.coinalert.coinalertappserver.Model.Comment;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Repository.CommentRepository;
import org.coinalert.coinalertappserver.Repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<Comment> getCommentsByPostId(Long postId){
        return postRepository.findById(postId)
                .map(commentRepository::findByPost)
                .orElse(Collections.EMPTY_LIST);
    }

    public Comment saveComment(CommentDTO commentDTO) throws PostNotFoundException {
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new PostNotFoundException("게시물을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setAuthor(commentDTO.getAuthor());
        comment.setContent(commentDTO.getContent());
        comment.setLikes(commentDTO.getLikes());
        comment.setLiked(false);

        commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return comment;
    }

    public void toggleLike(Long commentId, Boolean isLiked) throws CommentNotFoundException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        comment.setLikes(isLiked ? comment.getLikes() + 1 : comment.getLikes() - 1);
        comment.setLiked(isLiked);

        commentRepository.save(comment);
    }
}
