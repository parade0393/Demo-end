package me.parade.security.service;

import lombok.RequiredArgsConstructor;
import me.parade.security.utils.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限判断服务
 * <p>
 * 提供判断当前用户是否有权限访问特定接口的方法
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    /**
     * 判断当前用户是否具有指定权限
     *
     * @param permission 权限标识
     * @return 是否具有权限
     */
    public boolean hasPermission(String permission) {
        // 如果未提供权限标识，则直接返回false
        if (!StringUtils.hasText(permission)) {
            return false;
        }
        
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        // 获取当前用户的权限集合
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        }
        
        // 将权限转换为字符串集合
        Set<String> permissions = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        // 判断是否包含指定权限
        return permissions.contains(permission);
    }
    
    /**
     * 判断当前用户是否具有指定角色
     *
     * @param role 角色标识
     * @return 是否具有角色
     */
    public boolean hasRole(String role) {
        // 如果未提供角色标识，则直接返回false
        if (!StringUtils.hasText(role)) {
            return false;
        }

        return hasAuthority(role);
    }
    
    /**
     * 判断当前用户是否具有指定权限或角色
     *
     * @param authority 权限或角色标识
     * @return 是否具有权限或角色
     */
    public boolean hasAuthority(String authority) {
        // 如果未提供权限或角色标识，则直接返回false
        if (!StringUtils.hasText(authority)) {
            return false;
        }
        
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        // 获取当前用户的权限集合
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        }
        
        // 判断是否包含指定权限或角色
        return authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
    
    /**
     * 判断当前用户是否具有任意一个指定权限
     *
     * @param permissions 权限标识数组
     * @return 是否具有任意一个权限
     */
    public boolean hasAnyPermission(String... permissions) {
        // 如果未提供权限标识，则直接返回false
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        
        // 获取当前用户的权限集合
        Set<String> userPermissions = SecurityUtils.getAuthorities();
        if (CollectionUtils.isEmpty(userPermissions)) {
            return false;
        }
        
        // 判断是否包含任意一个指定权限
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 判断当前用户是否具有任意一个指定角色
     *
     * @param roles 角色标识数组
     * @return 是否具有任意一个角色
     */
    public boolean hasAnyRole(String... roles) {
        // 如果未提供角色标识，则直接返回false
        if (roles == null || roles.length == 0) {
            return false;
        }

        String[] authorities = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            authorities[i] = roles[i];
        }
        
        return hasAnyAuthority(authorities);
    }
    
    /**
     * 判断当前用户是否具有任意一个指定权限或角色
     *
     * @param authorities 权限或角色标识数组
     * @return 是否具有任意一个权限或角色
     */
    public boolean hasAnyAuthority(String... authorities) {
        // 如果未提供权限或角色标识，则直接返回false
        if (authorities == null || authorities.length == 0) {
            return false;
        }
        
        // 获取当前用户的权限集合
        Set<String> userAuthorities = SecurityUtils.getAuthorities();
        if (CollectionUtils.isEmpty(userAuthorities)) {
            return false;
        }
        
        // 判断是否包含任意一个指定权限或角色
        for (String authority : authorities) {
            if (userAuthorities.contains(authority)) {
                return true;
            }
        }
        
        return false;
    }
}