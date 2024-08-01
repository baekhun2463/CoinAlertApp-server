package org.coinalert.coinalertappserver.Controller;


import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Repository.UserRepository;
import org.coinalert.coinalertappserver.Service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user){
        log.info("user nickname : ", user.getNickName());
        log.info("user email : ", user.getEmail());
        log.info("user password : ", user.getPassword());

        if(userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }
        userService.save(user);
        return ResponseEntity.ok("회원가입 성공");
    }
}
