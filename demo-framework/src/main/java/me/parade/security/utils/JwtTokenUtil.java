package me.parade.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类，用于生成、解析、验证和刷新JWT令牌
 */
@Component
public class JwtTokenUtil {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 从token中获取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从token中获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从token中获取指定的claim
     *
     * @param token JWT令牌
     * @param claimsResolver claim解析函数
     * @param <T> 返回类型
     * @return claim值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从token中获取所有的claims
     *
     * @param token JWT令牌
     * @return 所有claims
     */
    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查token是否已过期
     *
     * @param token JWT令牌
     * @return 是否已过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成访问令牌
     *
     * @param userDetails 用户详情
     * @return 访问令牌
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 可以添加额外的用户信息到claims中
        return generateToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    }

    /**
     * 生成刷新令牌
     *
     * @param userDetails 用户详情
     * @return 刷新令牌
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userDetails.getUsername(), jwtConfig.getRefreshTokenExpiration());
    }

    /**
     * 生成令牌
     *
     * @param claims 额外的信息
     * @param subject 主题（通常是用户名）
     * @param expiration 过期时间（毫秒）
     * @return JWT令牌
     */
    private String generateToken(Map<String, Object> claims, String subject, long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 验证令牌
     *
     * @param token JWT令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 刷新令牌
     *
     * @param token 旧的JWT令牌
     * @return 新的JWT令牌
     */
    public String refreshToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration());
        
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
}