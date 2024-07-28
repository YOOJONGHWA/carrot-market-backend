package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.RoleEnum;
import com.example.carrotmarketbackend.Enum.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var result =  userRepository.findByEmail(email);
        if(result.isEmpty()) {
            throw  new UsernameNotFoundException(UserStatusEnum.USER_NOT_FOUND.message);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (Objects.equals(email, "admin@example.com")) {
            authorities.add(new SimpleGrantedAuthority(RoleEnum.ADMIN.getRole()));
        }
        else {
            authorities.add(new SimpleGrantedAuthority(RoleEnum.USER.getRole()));
        }

        var user = result.get();
        CustomUser customUser = new CustomUser(user.getUsername(),user.getPassword(),authorities);
        customUser.setId(user.getId());
        customUser.setEmail(user.getEmail());

        return customUser;


    }

}
