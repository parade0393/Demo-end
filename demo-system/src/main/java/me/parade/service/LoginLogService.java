package me.parade.service;

/**
 * 登录日志服务接口
 */
public interface LoginLogService {

    /**
     * 记录登录成功日志
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param ip IP地址
     */
    void recordLoginSuccess(Long userId, String username, String ip);

    /**
     * 记录登录失败日志
     *
     * @param username 用户名
     * @param message 失败消息
     * @param ip IP地址
     */
    void recordLoginFail(String username, String message, String ip);
}