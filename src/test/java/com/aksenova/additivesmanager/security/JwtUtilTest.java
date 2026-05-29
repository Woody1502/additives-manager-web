package com.aksenova.additivesmanager.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-32chars!!";
    private final JwtUtil jwtUtil = new JwtUtil();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
    }

    @Test
    void validateToken_validToken_returnsCorrectClaims() {
        String token = buildToken("alice", "ADMIN", System.currentTimeMillis() + 86400000L);

        var claims = jwtUtil.validateToken(token);

        assertThat(claims.getSubject()).isEqualTo("alice");
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
    }

    @Test
    void validateToken_expiredToken_throwsException() {
        String token = buildToken("bob", "USER", System.currentTimeMillis() - 1000L);

        assertThatThrownBy(() -> jwtUtil.validateToken(token))
                .isInstanceOf(Exception.class);
    }

    @Test
    void validateToken_invalidSignature_throwsException() {
        assertThatThrownBy(() -> jwtUtil.validateToken("invalid.jwt.token"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void validateToken_differentSecret_throwsException() {
        SecretKey wrongKey = Keys.hmacShaKeyFor("wrong-secret-key-that-is-32chars!".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("user")
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(wrongKey)
                .compact();

        assertThatThrownBy(() -> jwtUtil.validateToken(token))
                .isInstanceOf(Exception.class);
    }

    private String buildToken(String login, String role, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(login)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(expirationMs))
                .signWith(key)
                .compact();
    }
}
