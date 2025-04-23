package com.example.demo.config;

import com.example.demo.service.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JWTAuthenticationFilterTest {

    private JWTTokenService jwtTokenService;
    private JWTAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtTokenService = mock(JWTTokenService.class);
        filter = new JWTAuthenticationFilter(jwtTokenService);
    }

    @Test
    void shouldSkipAuthEndpoints() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/UrlShortener/auth/registration");

        assert filter.shouldNotFilter(request);
    }

    @Test
    void shouldSetAuthenticationWhenTokenValid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        String validToken = "Bearer VALID_TOKEN";

        when(request.getRequestURI()).thenReturn("/UrlShortener/users/getAllUsers");
        when(request.getHeader("Authorization")).thenReturn(validToken);
        when(jwtTokenService.validateToken("VALID_TOKEN")).thenReturn(true);
        when(jwtTokenService.getUsername("VALID_TOKEN")).thenReturn("john");

        filter.doFilterInternal(request, response, chain);

        verify(jwtTokenService).validateToken("VALID_TOKEN");
        verify(jwtTokenService).getUsername("VALID_TOKEN");
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldNotSetAuthenticationWhenTokenInvalid() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/UrlShortener/users/getAllUsers");
        when(request.getHeader("Authorization")).thenReturn("Bearer BAD_TOKEN");
        when(jwtTokenService.validateToken("BAD_TOKEN")).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        verify(jwtTokenService).validateToken("BAD_TOKEN");
        verify(jwtTokenService, never()).getUsername(any());
        verify(chain).doFilter(request, response);
    }

    @Test
    void shouldPassThroughWhenNoAuthorizationHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/UrlShortener/users/getAllUsers");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}


