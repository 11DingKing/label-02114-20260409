package com.menu.config;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT工具测试")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("生成Token")
    void testGenerateToken() {
        String token = jwtUtil.generateToken(1L, "admin", 1);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("解析Token获取用户ID")
    void testGetUserId() {
        String token = jwtUtil.generateToken(1L, "admin", 1);

        Long userId = jwtUtil.getUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("解析Token获取用户名")
    void testGetUsername() {
        String token = jwtUtil.generateToken(1L, "admin", 1);

        String username = jwtUtil.getUsername(token);

        assertEquals("admin", username);
    }

    @Test
    @DisplayName("解析Token获取角色")
    void testGetRole() {
        String token = jwtUtil.generateToken(1L, "admin", 1);

        Integer role = jwtUtil.getRole(token);

        assertEquals(1, role);
    }

    @Test
    @DisplayName("验证Token有效性")
    void testIsTokenValid() {
        String token = jwtUtil.generateToken(1L, "admin", 1);

        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Test
    @DisplayName("验证无效Token")
    void testIsTokenInvalid() {
        assertFalse(jwtUtil.isTokenValid("invalid.token.here"));
    }

    @Test
    @DisplayName("解析Token获取Claims")
    void testParseToken() {
        String token = jwtUtil.generateToken(2L, "girlfriend", 0);

        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims);
        assertEquals("girlfriend", claims.getSubject());
        assertEquals(2L, claims.get("userId", Long.class));
        assertEquals(0, claims.get("role", Integer.class));
    }
}
