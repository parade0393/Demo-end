package me.parade.service;

import me.parade.domain.dto.auth.LoginResponse;
import me.parade.domain.dto.auth.UserInfoResponse;

/**
 * 认证服务接口
 * 提供用户登录、获取用户信息等功能
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录响应（包含token、刷新token和过期时间）
     */
    LoginResponse login(String username, String password);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息（包含基本信息、权限和角色）
     */
    UserInfoResponse getCurrentUserInfo();
    
    /**
     * 用户注销
     * 清除当前用户的认证信息
     */
    void logout();

    /**
     * 根据刷新令牌获取新的访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录响应（包含新的token、刷新token和过期时间）
     */
    LoginResponse refreshToken(String refreshToken);
}