package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JWTTokenServiceTest {

    private JWTTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JWTTokenService();
        ReflectionTestUtils.setField(jwtTokenService, "secretKey", "test-secret-key");
    }

    @Test
    void testCreateAndValidateToken() {
        String username = "john";
        String token = jwtTokenService.createToken(username);

        assertThat(token).isNotNull();
        assertThat(jwtTokenService.validateToken(token)).isTrue();
    }

    @Test
    void testGetUsernameFromToken() {
        String username = "alice";
        String token = jwtTokenService.createToken(username);
        String extractedUsername = jwtTokenService.getUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "this.is.not.a.jwt";
        boolean isValid = jwtTokenService.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void testExpiredToken() throws InterruptedException {

            Algorithm algorithm = Algorithm.HMAC256("test-secret-key");

            String expiredToken = JWT.create()
                    .withSubject("bob")
                    .withIssuedAt(new Date(System.currentTimeMillis() - 10000))
                    .withExpiresAt(new Date(System.currentTimeMillis() - 5000))
                    .sign(algorithm);

            JWTTokenService service = new JWTTokenService();
            ReflectionTestUtils.setField(service, "secretKey", "test-secret-key");

            boolean isValid = service.validateToken(expiredToken);

            assertThat(isValid).isFalse();


    }
}
