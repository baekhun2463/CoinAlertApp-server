package org.coinalert.coinalertappserver.Controller;

import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.JwtResponse;
import org.coinalert.coinalertappserver.Model.Member;
import org.coinalert.coinalertappserver.Repository.MemberRepository;
import org.coinalert.coinalertappserver.Service.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, MemberRepository memberRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.memberRepository = memberRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    private boolean userExists(String email) {
        Optional<Member> user = memberRepository.findByEmail(email);
        return user.isPresent();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Member member) {
        if (userExists(member.getEmail())) {
            return ResponseEntity.badRequest().body("이메일이 이미 있습니다.");
        }
        userService.registerUser(member);
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
            String jwt = jwtUtil.generateAccessToken(authentication);

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
        log.info(userDetails.getUsername());
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }

        String identifier = userDetails.getUsername();

        Member member = memberRepository.findByEmail(identifier)
                .orElseGet(() -> memberRepository.findByNickname(identifier)
                        .orElse(null));

        if(member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        memberRepository.delete(member);

        return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다");
    }


}

