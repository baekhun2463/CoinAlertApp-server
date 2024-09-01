package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.JwtResponse;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.coinalert.coinalertappserver.Util.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class GithubLoginController {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    @PostMapping("/github-login")
    public ResponseEntity<?> githubLogin(@RequestBody Member member) {

        Optional<Member> existingMember = memberRepository.findByNickname(member.getNickname());

        if(existingMember.isPresent()) {
            existingMember.get().setLastLogin(LocalDateTime.now());
            String jwt = jwtUtil.generateTokenOauth2(existingMember.get().getId().toString());
            return ResponseEntity.ok(new JwtResponse(jwt));
        }else {
            Member newMember = Member.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .oauth2Provider("GitHub")
                    .avatar_url(member.getAvatar_url())
                    .role(Role.USER)
                    .build();

            memberRepository.save(newMember);

            String jwt = jwtUtil.generateTokenOauth2(newMember.getId().toString());
            return ResponseEntity.ok(new JwtResponse(jwt));
        }
    }
    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> handleLoginSuccess(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String username = oauth2User.getAttribute("login");
        String email = oauth2User.getAttribute("email");
        String avatarUrl = oauth2User.getAttribute("avatar_url");

        return ResponseEntity.ok(oauth2User.getAttributes());
    }

    @GetMapping("/failure")
    public ResponseEntity<String> handleLoginFailure() {
        return ResponseEntity.status(401).body("GitHub 로그인 실패");
    }
}
