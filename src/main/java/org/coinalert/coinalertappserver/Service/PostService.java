package org.coinalert.coinalertappserver.Service;

import lombok.RequiredArgsConstructor;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.Post;
import org.coinalert.coinalertappserver.Repository.CommentRepository;
import org.coinalert.coinalertappserver.Repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    public Post savePost(Post post, UserDetails userDetails) {
        Member member = memberService.findMember(userDetails.getUsername());

        post.setMember(member);
        post.setAuthor(member.getNickname());
        post.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        post.setAvatar_url(member.getAvatar_url());

        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public boolean toggleLike(Map<String, Object> likeData) {
        Long postId = ((Number) likeData.get("postId")).longValue();
        Boolean isLiked = (Boolean) likeData.get("isLiked");

        Optional<Post> postOptional = postRepository.findById(postId);
        if(postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setLikes(isLiked ? post.getLikes() + 1 : post.getLikes() - 1);
            postRepository.save(post);
            return true;
        }else {
            return false;
        }
    }
}
