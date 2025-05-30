package com.example.demo.config;

import com.example.demo.service.JWTTokenService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private UserService userService;
    private JWTTokenService jwtTokenService;
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtTokenService = mock(JWTTokenService.class);
        jwtAuthenticationFilter = mock(JWTAuthenticationFilter.class);
        securityConfig = new SecurityConfig(jwtAuthenticationFilter);
    }

    @Test
    void passwordEncoderShouldBeBCrypt() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String encoded = encoder.encode("test123");
        assertNotNull(encoded);
        assertTrue(encoder.matches("test123", encoded));
    }

    @Test
    void authenticationManagerShouldBeCreated() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);

        when(config.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = securityConfig.authenticationManager(config);
        assertSame(manager, result);
    }


}

