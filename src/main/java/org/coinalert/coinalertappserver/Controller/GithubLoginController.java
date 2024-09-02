package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.JwtResponse;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.coinalert.coinalertappserver.Util.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
}
