package org.coinalert.coinalertappserver.Service;

import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isNickNameTaken(String nickName) {
        return userRepository.findByNickName(nickName).isPresent();
    }
}
