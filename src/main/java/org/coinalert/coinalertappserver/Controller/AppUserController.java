package org.coinalert.coinalertappserver.Controller;

import lombok.extern.slf4j.Slf4j;

import org.coinalert.coinalertappserver.Model.AppUser;
import org.coinalert.coinalertappserver.Repository.AppUserRepository;
import org.coinalert.coinalertappserver.Service.AppUserService;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AppUserController {
    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> regsterUser(@RequestBody AppUser appUser) {
        AppUser savedAppUser = null;
        ResponseEntity response = null;
        try {
            String hasedPwd = passwordEncoder.encode(appUser.getPassword());
            appUser.setPassword(hasedPwd);
            appUser.setCreate_at(String.valueOf(new Date(System.currentTimeMillis())));
            savedAppUser = appUserRepository.save(appUser);
            if(Objects.equals(savedAppUser.getEmail(), appUser.getEmail())) {
                response = ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("성공적으로 회원가입 되었습니다.");
            }
        }catch (Exception e) {
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예외 상황 " + e.getMessage());
        }
        return response;
    }

}
