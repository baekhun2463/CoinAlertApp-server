package org.coinalert.coinalertappserver.DTO;

import lombok.Getter;
import lombok.Setter;
import org.coinalert.coinalertappserver.Model.Comment;
import org.coinalert.coinalertappserver.Model.Post;

import java.util.List;

@Getter
@Setter
public class MemberDataResponseDTO {
    private String nickname;
    private String avatarUrl;
    private List<Post> posts;
    private List<Comment> comments;

    public MemberDataResponseDTO(String nickname, String avatarUrl, List<Post> posts, List<Comment> comments) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.posts = posts;
        this.comments = comments;
    }
}
