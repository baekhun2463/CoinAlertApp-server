package org.coinalert.coinalertappserver.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTO {

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("member_id")
    private Long memberId;

    private String content;
    private String author;
    private int likes;
}
