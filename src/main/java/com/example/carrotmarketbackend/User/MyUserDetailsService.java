package com.example.carrotmarketbackend.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        DB에서 username을 가진 유저를 찾아와서
//        return new User(유저아이디, 비번, 권한) 해주세요
        var result =  userRepository.findByEmail(email);
        if(result.isEmpty()) {
            throw  new UsernameNotFoundException("그런 유저가 없음");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (Objects.equals(email, "admin@example.com")) {
            authorities.add(new SimpleGrantedAuthority("관리자"));
        }
        else {
            authorities.add(new SimpleGrantedAuthority("일반유저"));
        }

        var user = result.get();
        CustomUser customUser = new CustomUser(user.getUsername(),user.getPassword(),authorities);
        customUser.setId(user.getId());
        customUser.setEmail(user.getEmail());

        return customUser;


    }

}
