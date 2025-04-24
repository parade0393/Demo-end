package me.parade.service;

import java.util.Set;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 验证用户是否具备某权限
     *
     * @param permission 权限字符串
     * @return 是否具备权限
     */
    boolean hasPermission(String permission);

    /**
     * 验证用户是否具备某角色
     *
     * @param role 角色标识
     * @return 是否具备角色
     */
    boolean hasRole(String role);

    /**
     * 获取当前用户的角色列表
     *
     * @return 角色标识集合
     */
    Set<String> getRoleKeys();

    /**
     * 获取当前用户的权限列表
     *
     * @return 权限标识集合
     */
    Set<String> getPermissions();

    /**
     * 判断是否为管理员
     *
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 判断用户是否具有某个角色
     *
     * @param roleKey 角色标识
     * @return 是否具有该角色
     */
    boolean hasRoleKey(String roleKey);

    /**
     * 判断用户是否具有某个权限
     *
     * @param permission 权限标识
     * @return 是否具有该权限
     */
    boolean hasPermissionKey(String permission);
}