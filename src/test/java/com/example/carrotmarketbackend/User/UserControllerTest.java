package com.example.carrotmarketbackend.User;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void loginJwt_success() throws Exception {
        // Given
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        String jwt = "mockJwtToken";
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.createToken(any(Authentication.class))).thenReturn(jwt);

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andReturn();

        // Then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(jwt, response.getContentAsString());
        assertNotNull(response.getCookie("jwt"));
        Cookie cookie = response.getCookie("jwt");
        assertNotNull(cookie);
        assertEquals("jwt", cookie.getName());
        assertEquals(jwt, cookie.getValue());
        assertEquals(3600, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.getSecure());
    }

    @Test
    void loginJwt_failure() throws Exception {
        // Given
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("wrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\", \"password\":\"wrongPassword\"}"))
                .andReturn();

        // Then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("Error occurred"));
    }
}
