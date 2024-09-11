package org.coinalert.coinalertappserver.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private int likes;
    private boolean isLiked;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = true)
    @JsonBackReference // 순환 참조 방지
    private Post post;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = true)
    @JsonIgnore // 멤버 객체를 직렬화에서 제외
    private Member member;

    @JsonProperty("author")
    public String getAuthor() {
        return member != null ? member.getNickname() : "Unknown";
    }
}
