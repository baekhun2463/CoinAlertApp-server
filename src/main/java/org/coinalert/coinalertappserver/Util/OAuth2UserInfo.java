package org.coinalert.coinalertappserver.Util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.security.auth.message.AuthException;
import lombok.Builder;
import org.coinalert.coinalertappserver.Model.OAuth2Member;


import java.util.Map;

@Builder
public record OAuth2UserInfo(
    String name,
    String email,
    String profile
) {
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) throws AuthException {
        return switch(registrationId) {
            case "github" -> ofGithub(attributes);
            default -> throw new AuthException("OAuth error");
        };
    }

    private static OAuth2UserInfo ofGithub(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("login")) // GitHub의 로그인 이름
                .email((String) attributes.get("email")) // 이메일이 null일 수도 있음
                .profile((String) attributes.get("avatar_url")) // 프로필 사진 URL
                .build();
    }

    public OAuth2Member toEntity() {
        return OAuth2Member.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .memberKey(String.valueOf(Keys.secretKeyFor(SignatureAlgorithm.HS512)))
                .role(String.valueOf(Role.USER))
                .build();
    }

}
