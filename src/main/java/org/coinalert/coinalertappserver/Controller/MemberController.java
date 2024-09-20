package org.coinalert.coinalertappserver.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.DTO.JwtResponseDTO;
import org.coinalert.coinalertappserver.DTO.MemberDataResponseDTO;
import org.coinalert.coinalertappserver.DTO.MemberResponseDTO;
import org.coinalert.coinalertappserver.DTO.ResetPasswordRequestDTO;
import org.coinalert.coinalertappserver.Exception.UnauthorizedException;
import org.coinalert.coinalertappserver.Exception.UserNotFoundException;
import org.coinalert.coinalertappserver.Model.*;
import org.coinalert.coinalertappserver.Service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Member member) {
        boolean isRegistered = memberService.checkIfUserExistsAndRegister(member);
        if(!isRegistered) {
            return ResponseEntity.badRequest().body("이메일이 이미 있습니다.");
        }

        return ResponseEntity.ok("성공적으로 회원가입이 되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Member member) throws BadCredentialsException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = memberService.authenticateAndGenerateToken(member, authentication);
            return ResponseEntity.ok(new JwtResponseDTO(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 이름 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            log.error("Authentication failed: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 요청입니다.");
        }
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            memberService.deleteMember(userDetails);
            return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
        }catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/findEmailByNickName")
    public ResponseEntity<?> findEmailByNickName(@RequestBody Map<String, String> payload) {
        String nickName = payload.get("nickName");

        if (nickName == null || nickName.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("닉네임을 입력해주세요.");
        }

        return memberService.findEmailByNickname(nickName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("닉네임이 올바르지 않습니다."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        memberService.resetPassword(resetPasswordRequestDTO.getEmail(), resetPasswordRequestDTO.getPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");
    }

    @PostMapping("/updateNickname")
    public ResponseEntity<?> updateNickname(@RequestBody Map<String, String> nickname, @AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자.");
        }

        String newNickname = nickname.get("nickname");
        if(newNickname == null || newNickname.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 닉네임입니다.");
        }

        memberService.updateNickname(newNickname, userDetails.getUsername());
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }

    @GetMapping("/getNickname")
    public ResponseEntity<?> getNickname(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증되지 않은 사용자입니다.");
        }
        MemberResponseDTO response = memberService.getNickname(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMemberData")
    public ResponseEntity<?> getMemberData(@AuthenticationPrincipal UserDetails userDetails) {

        if(userDetails == null) {
            return new ResponseEntity<>("유효하지않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        MemberDataResponseDTO response = memberService.getMemberData(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/updateAvatarUrl")
    public ResponseEntity<String> updateAvatarUrl(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody Map<String, String> requestBody) {
        if(userDetails == null) {
            return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
        }

        String newAvatarUrl = requestBody.get("avatar_url");
        if(newAvatarUrl == null || newAvatarUrl.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 URL입니다.");
        }

        try {
            memberService.updateAvatarUrl(userDetails.getUsername(), newAvatarUrl);
            return ResponseEntity.ok("프로필 사진이 성공적으로 업데이트 되었습니다.");
        }catch(UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }
}

