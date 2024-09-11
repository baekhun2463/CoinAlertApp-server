package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.JwtResponse;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Model.MemberResponse;
import org.coinalert.coinalertappserver.Model.ResetPasswordRequestDTO;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Service.MemberService;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Member member) {
        Optional<Member> user = memberRepository.findByEmail(member.getEmail());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body("이메일이 이미 있습니다.");
        }
        memberService.registerUser(member);
        return ResponseEntity.ok("성공적으로 회원가입이 되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member) throws BadCredentialsException {
        try {
            // 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword()));

            // 인증된 사용자로 SecurityContext 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());

            Member foundMember = optionalMember.orElseThrow(() -> new UsernameNotFoundException("존재하지 않은 회원입니다."));

            foundMember.setLastLogin(LocalDateTime.now());

            memberRepository.save(foundMember);

            // JWT 토큰 생성
            String jwt = jwtUtil.generateToken(authentication);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException e) {
            // 잘못된 사용자 자격 증명 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 이름 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            // 그 외 인증 실패 처리
            log.error("Authentication failed: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 요청입니다.");
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String identifier = userDetails.getUsername();
        Member member = memberRepository.findByEmail(identifier)
                .orElseGet(() -> memberRepository.findByOauth2Id(Long.valueOf(identifier))
                        .orElse(null));

        if(member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        memberRepository.delete(member);

        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다");
    }

    @PostMapping("/findEmailByNickName")
    public ResponseEntity<?> findEmailByNickName(@RequestBody Map<String, String> payload) {
        String nickName = payload.get("nickName");

        if (nickName == null || nickName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("닉네임을 입력해주세요.");
        }

        Optional<Member> member = memberRepository.findByNickname(nickName);

        if (member.isPresent()) {
            String email = member.get().getEmail();
            return ResponseEntity.ok(email);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("닉네임이 올바르지 않습니다.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        Optional<Member> memberOptional = memberRepository.findByEmail(resetPasswordRequestDTO.getEmail());

        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();

            member.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getPassword()));
            memberRepository.save(member);
            return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이메일을 찾을 수 없습니다.");
        }
    }

    @PostMapping("/updateNickname")
    public ResponseEntity<?> updateNickname(@RequestBody Map<String, String> nickname, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String newNickname = nickname.get("nickname");
        if (newNickname == null || newNickname.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 닉네임입니다.");
        }

        if(memberRepository.existsByNickname(newNickname)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 닉네임이 있습니다.");
        }

        Optional<Member> memberOptional = memberRepository.findByEmail(userDetails.getUsername())
                .or(() -> memberRepository.findByOauth2Id(Long.valueOf(userDetails.getUsername())));

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            member.setNickname(newNickname);
            memberRepository.save(member);
            return ResponseEntity.ok("닉네임이 변경되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/getNickname")
    public ResponseEntity<?> getNickname(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String username = userDetails.getUsername();
        Optional<Member> memberOptional = memberRepository.findByEmail(username)
                .or(() -> memberRepository.findByOauth2Id(Long.valueOf(username)));

        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            String nickname = member.getNickname();
            Long memberId = member.getId();
            return ResponseEntity.ok(new MemberResponse(nickname, memberId));
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }


    }

}

