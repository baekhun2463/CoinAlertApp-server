package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.DTO.JwtResponseDTO;
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
        log.info("GitHub User Info: {}", member);

        // 이메일을 기준으로 회원을 찾습니다.
        Optional<Member> existingMember = memberRepository.findByEmail(member.getEmail());

        if(existingMember.isPresent()) {
            // 기존 회원이 존재하는 경우, 마지막 로그인 시간 업데이트
            Member existing = existingMember.get();
            log.info("Existing Member Info: {}", existing.getAvatar_url());
            existing.setLastLogin(LocalDateTime.now());
            memberRepository.save(existing); // 업데이트된 정보를 저장

            // JWT 토큰 생성
            String jwt = jwtUtil.generateTokenOauth2(existing.getEmail()); // 이메일을 사용하여 JWT 생성
            return ResponseEntity.ok(new JwtResponseDTO(jwt));
        } else {
            log.info("New Member Registration: {}", member.getAvatar_url());

            // 새로운 회원 등록
            Member newMember = Member.builder()
                    .nickname(member.getNickname())
                    .email(member.getEmail())
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .oauth2Provider("GitHub")
                    .oauth2Id(member.getId()) // GitHub로부터 받은 ID를 설정
                    .avatar_url(member.getAvatar_url())
                    .role(Role.USER)
                    .build();

            // 새 멤버를 저장
            memberRepository.save(newMember);

            // JWT 토큰 생성
            String jwt = jwtUtil.generateTokenOauth2(newMember.getEmail()); // 이메일을 사용하여 JWT 생성
            return ResponseEntity.ok(new JwtResponseDTO(jwt));
        }
    }
}
