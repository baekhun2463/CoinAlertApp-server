package org.coinalert.coinalertappserver.Config;

import org.coinalert.coinalertappserver.Model.AppUser;
import org.coinalert.coinalertappserver.Repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoinAlertAppUserDetails implements UserDetailsService {

    @Autowired
    AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userName, password = null;
        List<GrantedAuthority> authorities = null;
        List<AppUser> appUser = appUserRepository.findByEmail(username);
        if(appUser.isEmpty()) {
            throw new UsernameNotFoundException("유효하지 않은 이메일입니다. : " + username);
        }else {
            userName = appUser.get(0).getEmail();
            password = appUser.get(0).getPassword();
            authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(appUser.get(0).getRole()));
        }
        return new User(username, password, authorities);
    }
}
