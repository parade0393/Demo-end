package me.parade.security.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类，用于配置JWT相关参数
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    /**
     * JWT加密密钥
     */
    private String secret = "parade_jwt_secret_key";
    
    /**
     * 访问令牌过期时间（毫秒）
     * 默认30分钟
     */
    private long accessTokenExpiration = 1800000;
    
    /**
     * 刷新令牌过期时间（毫秒）
     * 默认7天
     */
    private long refreshTokenExpiration = 604800000;
    
    /**
     * 令牌前缀
     */
    private String tokenPrefix = "Bearer ";
    
    /**
     * 请求头名称
     */
    private String headerString = "Authorization";
    
    /**
     * 令牌发行者
     */
    private String issuer = "parade";
}