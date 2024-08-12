package org.coinalert.coinalertappserver.Service;

import org.coinalert.coinalertappserver.Model.AppUser;
import org.coinalert.coinalertappserver.Repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AppUser saveUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

//    public boolean isEmailTaken(String email) {
//        return userRepository.findByEmail(email);
//    }

//    public boolean isNickNameTaken(String nickName) {
//        return appUserRepository.findByNickName(nickName).isPresent();
//    }
}
