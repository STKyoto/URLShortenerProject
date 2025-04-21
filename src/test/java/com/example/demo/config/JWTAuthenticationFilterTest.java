package com.example.demo.config;

import com.example.demo.service.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

    class JWTAuthenticationFilterTest {

        @InjectMocks
        private JWTAuthenticationFilter jwtAuthenticationFilter;

        @Mock
        private JWTTokenService jwtTokenService;

        @Mock
        private HttpServletRequest request;

        @Mock
        private HttpServletResponse response;

        @Mock
        private FilterChain filterChain;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            SecurityContextHolder.clearContext(); // очищуємо контекст перед кожним тестом
        }

        @Test
        void shouldAuthenticateWhenTokenIsValid() throws IOException, ServletException {
            String token = "Bearer valid.jwt.token";
            String rawToken = "valid.jwt.token";

            when(request.getHeader("Authorization")).thenReturn(token);
            when(jwtTokenService.validateToken(rawToken)).thenReturn(true);
            when(jwtTokenService.getUsername(rawToken)).thenReturn("testUser");

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            assertNotNull(authentication);
            assertEquals("testUser", authentication.getPrincipal());
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        void shouldNotAuthenticateWhenTokenIsInvalid() throws IOException, ServletException {
            String token = "Bearer invalid.token";

            when(request.getHeader("Authorization")).thenReturn(token);
            when(jwtTokenService.validateToken("invalid.token")).thenReturn(false);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        void shouldNotAuthenticateWhenNoTokenProvided() throws IOException, ServletException {
            when(request.getHeader("Authorization")).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            assertNull(SecurityContextHolder.getContext().getAuthentication());
            verify(filterChain, times(1)).doFilter(request, response);
        }
    }


