package org.coinalert.coinalertappserver.Controller;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

           userService.saveUser(user);
           return ResponseEntity.ok("회원가입 완료");
       }
    }
}
