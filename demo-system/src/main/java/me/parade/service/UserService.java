package me.parade.service;

import me.parade.domain.entity.SysUser;
import me.parade.domain.vo.UserInfoVO;

import java.util.Set;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    Set<String> getUserRoles(Long userId);
}