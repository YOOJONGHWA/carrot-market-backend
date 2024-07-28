package com.example.carrotmarketbackend.User;

import com.example.carrotmarketbackend.Enum.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("user@example.com");
    }

    @Test
    void shouldLoadUserByUsername() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        CustomUser customUser = (CustomUser) myUserDetailsService.loadUserByUsername("user@example.com");

        assertThat(customUser).isNotNull();
        assertThat(customUser.getUsername()).isEqualTo("testuser");
        assertThat(customUser.getEmail()).isEqualTo("user@example.com");
        assertThat(customUser.getAuthorities()).hasSize(1);
        assertThat(customUser.getAuthorities().iterator().next().getAuthority()).isEqualTo(RoleEnum.USER.getRole());
    }

    @Test
    void shouldAssignAdminRole() {
        user.setEmail("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        CustomUser customUser = (CustomUser) myUserDetailsService.loadUserByUsername("admin@example.com");

        assertThat(customUser).isNotNull();
        assertThat(customUser.getUsername()).isEqualTo("testuser");
        assertThat(customUser.getEmail()).isEqualTo("admin@example.com");
        assertThat(customUser.getAuthorities()).hasSize(1);
        assertThat(customUser.getAuthorities().iterator().next().getAuthority()).isEqualTo(RoleEnum.ADMIN.getRole());
    }
}
