package me.parade.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO {

    /**
     * 访问token
     */
    private String token;

    /**
     * 刷新token
     */
    private String refreshToken;

    /**
     * 过期时间（毫秒）
     */
    private Long expireTime;
}