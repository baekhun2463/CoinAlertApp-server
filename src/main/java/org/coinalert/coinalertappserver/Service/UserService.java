package org.coinalert.coinalertappserver.Service;

import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    final private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User save(User user) {
        User newUser = new User();
        newUser.setNickName(user.getNickName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(newUser);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
