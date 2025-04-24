package me.parade.service.impl;


import lombok.RequiredArgsConstructor;
import me.parade.domain.dto.LoginUser;
import me.parade.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 权限服务实现类
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private static final Long ADMIN_USER_ID = 1L;
    private static final String ADMIN_ROLE_KEY = "admin";

    @Override
    public boolean hasPermission(String permission) {
        if (permission == null || permission.isEmpty()) {
            return false;
        }

        // 获取当前用户权限列表
        Set<String> permissions = getPermissions();
        if (CollectionUtils.isEmpty(permissions)) {
            return false;
        }

        return permissions.contains(permission);
    }

    @Override
    public boolean hasRole(String role) {
        if (role == null || role.isEmpty()) {
            return false;
        }

        // 获取当前用户角色列表
        Set<String> roles = getRoleKeys();
        if (CollectionUtils.isEmpty(roles)) {
            return false;
        }

        return roles.contains(role);
    }

    @Override
    public Set<String> getRoleKeys() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return Set.of();
        }
        return loginUser.getRoles();
    }

    @Override
    public Set<String> getPermissions() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return Set.of();
        }
        return loginUser.getPermissions();
    }

    @Override
    public boolean isAdmin(Long userId) {
        return ADMIN_USER_ID.equals(userId) || hasRoleKey(ADMIN_ROLE_KEY);
    }

    @Override
    public boolean hasRoleKey(String roleKey) {
        return hasRole(roleKey);
    }

    @Override
    public boolean hasPermissionKey(String permission) {
        return hasPermission(permission);
    }

    /**
     * 获取当前登录用户
     */
    private LoginUser getLoginUser() {
        // TODO: 2025/4/24 获取当前登录用户 

        return null;
    }
}