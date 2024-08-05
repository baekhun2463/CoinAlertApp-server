package org.coinalert.coinalertappserver.Service;

import lombok.extern.slf4j.Slf4j;
import org.coinalert.coinalertappserver.Model.User;
import org.coinalert.coinalertappserver.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> OptionalUser = userRepository.findByEmail(email);
        log.info(OptionalUser.get().getEmail());
        if (OptionalUser.isEmpty()) {
            throw new UsernameNotFoundException("이메일을 찾을 수 없습니다 : " + email);
        }
        User user = OptionalUser.get();
        log.info(user.getEmail(), user.getNickName(), user.getPassword());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
