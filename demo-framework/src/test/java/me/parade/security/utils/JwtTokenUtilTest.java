package me.parade.security.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * JWT工具类测试
 */
public class JwtTokenUtilTest {

    @InjectMocks
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private JwtConfig jwtConfig;

    private UserDetails userDetails;
    private final String username = "testuser";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetails = new User(username, "password", new ArrayList<>());
        
        // 配置Mock对象
        when(jwtConfig.getSecret()).thenReturn("test_secret_key_1234567890_abcdefg_123456");
        when(jwtConfig.getAccessTokenExpiration()).thenReturn(3600000L); // 1小时
        when(jwtConfig.getRefreshTokenExpiration()).thenReturn(86400000L); // 1天
    }

    @Test
    public void testGenerateAccessToken() {
        String token = jwtTokenUtil.generateAccessToken(userDetails);
        System.out.println(token);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testGetUsernameFromToken() {
        String token = jwtTokenUtil.generateAccessToken(userDetails);
        String extractedUsername = jwtTokenUtil.getUsernameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testValidateToken() {
        String token = jwtTokenUtil.generateAccessToken(userDetails);
        boolean isValid = jwtTokenUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    public void testRefreshToken() {
        String originalToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshedToken = jwtTokenUtil.refreshToken(originalToken);
        
        assertNotNull(refreshedToken);
        assertNotEquals(originalToken, refreshedToken);
        
        // 验证刷新后的token仍然有效
        String usernameFromRefreshedToken = jwtTokenUtil.getUsernameFromToken(refreshedToken);
        assertEquals(username, usernameFromRefreshedToken);
    }

    @Test
    public void testGetExpirationDateFromToken() {
        String token = jwtTokenUtil.generateAccessToken(userDetails);
        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
        
        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }
}