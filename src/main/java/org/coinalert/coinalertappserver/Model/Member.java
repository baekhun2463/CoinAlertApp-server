package org.coinalert.coinalertappserver.Model;

import jakarta.persistence.*;
import lombok.*;
import org.coinalert.coinalertappserver.Util.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 유저 고유 ID

    @Column(unique = true)
    private String email; // 이메일 (모든 사용자에게 공통)

    @Column(unique = true)
    private String nickname; // 이름 (모든 사용자에게 공통)

    private String password; // 비밀번호 (일반 회원가입 유저만)

    private String oauth2Provider; // OAuth2 제공자 정보 (OAuth2 회원가입 유저만)

    private Long oauth2Id;

    private LocalDateTime createdAt; // 계정 생성 날짜 (모든 사용자에게 공통)

    private LocalDateTime lastLogin;

    private String avatar_url;

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 권한 (예: ADMIN, USER 등)

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}