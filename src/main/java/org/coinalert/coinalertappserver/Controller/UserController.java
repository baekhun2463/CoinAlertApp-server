package org.coinalert.coinalertappserver.Controller;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.AuthenticationRequest;
import org.coinalert.coinalertappserver.Model.AuthenticationResponse;
import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Service.MyUserDetailsService;
import org.coinalert.coinalertappserver.Service.UserService;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody User user) {
        log.info("회원가입 요청 수신: {}", user);

       if(userService.isEmailTaken(user.getEmail())) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다");
       } else if (userService.isNickNameTaken(user.getNickName())){
           return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 닉네임입니다.");
       }else {
           User newUser = new User(
                   user.getNickName(),
                   user.getEmail(),
                   user.getPassword()
           );

           userService.saveUser(newUser);
           return ResponseEntity.ok("회원가입 완료");
       }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
            );
        }catch (BadCredentialsException e) {
            throw new Exception("이메일 혹은 비밀번호가 정확하지 않습니다.",e);
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
