package me.parade.security.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类，用于配置JWT相关参数
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    /**
     * JWT加密密钥
     */
    private String secret;
    
    /**
     * 访问令牌过期时间（毫秒）
     * 默认30分钟
     */
    private long accessTokenExpiration;
    
    /**
     * 刷新令牌过期时间（毫秒）
     * 默认7天
     */
    private long refreshTokenExpiration;
    
    /**
     * 令牌前缀
     */
    private String tokenPrefix;
    
    /**
     * 请求头名称
     */
    private String headerString;
    
    /**
     * 令牌发行者
     */
    private String issuer;

}