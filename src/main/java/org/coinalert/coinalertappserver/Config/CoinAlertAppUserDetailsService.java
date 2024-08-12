package org.coinalert.coinalertappserver.Config;

import lombok.RequiredArgsConstructor;
import org.coinalert.coinalertappserver.Model.AppUser;
import org.coinalert.coinalertappserver.Repository.AppUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinAlertAppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(username).orElseThrow(() -> new
                UsernameNotFoundException("일치하는 회원을 찾을 수 없습니다. : " + username));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(appUser.getRole()));
        return new User(appUser.getEmail(), appUser.getPassword(), authorities);
    }
}
