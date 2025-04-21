package com.example.demo.service;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private AuthenticationManager authenticationManager;
    private JWTTokenService jwtTokenService;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtTokenService = mock(JWTTokenService.class);
        authenticationService = new AuthenticationService(authenticationManager, jwtTokenService);
    }

    @Test
    void testAuthenticateUser() {

        AuthRequest request = new AuthRequest("john", "pass123");
        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(mockAuth.getName()).thenReturn("john");
        when(jwtTokenService.createToken("john")).thenReturn("mocked-jwt-token");

        AuthResponse response = authenticationService.authenticateUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken tokenUsed = captor.getValue();
        assertThat(tokenUsed.getName()).isEqualTo("john");
        assertThat(tokenUsed.getCredentials()).isEqualTo("pass123");

        verify(jwtTokenService).createToken("john");
    }
}
