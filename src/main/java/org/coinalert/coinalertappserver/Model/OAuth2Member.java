package org.coinalert.coinalertappserver.Model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class OAuth2Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String profile;

    private String memberKey;

    private String role;

    @Builder
    public OAuth2Member(String name, String email, String profile, String memberKey, String role) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.memberKey = memberKey;
        this.role = role;
    }
}
