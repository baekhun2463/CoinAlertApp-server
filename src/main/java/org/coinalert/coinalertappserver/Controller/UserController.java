package org.coinalert.coinalertappserver.Controller;

import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.JwtResponse;
import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Repository.UserRepository;
import org.coinalert.coinalertappserver.Service.UserService;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    private boolean userExists(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userExists(user.getEmail())) {
            return ResponseEntity.badRequest().body("이메일이 이미 있습니다.");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("성공적으로 회원가입이 되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws BadCredentialsException {
        try {
            // 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            // 인증된 사용자로 SecurityContext 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);

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



}

