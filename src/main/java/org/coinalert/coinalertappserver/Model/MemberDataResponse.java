package org.coinalert.coinalertappserver.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemberDataResponse {
    private String nickname;
    private String avatarUrl;
    private List<Post> posts;
    private List<Comment> comments;

    public MemberDataResponse(String nickname, String avatarUrl, List<Post> posts, List<Comment> comments) {
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.posts = posts;
        this.comments = comments;
    }
}
