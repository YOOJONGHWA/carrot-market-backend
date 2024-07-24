package com.example.carrotmarketbackend.User;

import lombok.AllArgsConstructor;
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
           throw  new UsernameNotFoundException("그런 아이디 없음");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (Objects.equals(email, "test123")) {
            authorities.add(new SimpleGrantedAuthority("관리자"));
        }
        else {
            authorities.add(new SimpleGrantedAuthority("일반유저"));
        }
        var user = result.get();
        var users = new CustomUser(user.getEmail(),user.getPassword(),authorities);
        users.username = user.getUsername();
        users.id = user.getId();
        return users;


    }

}
