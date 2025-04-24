package me.parade.security.service;


import me.parade.domain.dto.LoginUser;

/**
 * 登录服务接口
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录用户信息
     */
    LoginUser login(String username, String password);

    /**
     * 登出
     */
    void logout();

    /**
     * 刷新Token
     *
     * @param token 原token
     * @return 新token
     */
    String refreshToken(String token);
}