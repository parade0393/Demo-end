package me.parade.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security工具类
 * <p>
 * 提供获取当前用户认证信息的工具方法
 */
public class SecurityUtils {

    /**
     * 获取当前用户的认证信息
     *
     * @return 认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * 获取当前登录用户详情
     *
     * @return 用户详情
     */
    public static UserDetails getUserDetails() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return (UserDetails) principal;
        }
        return null;
    }
    
    /**
     * 获取当前用户的权限列表
     *
     * @return 权限列表
     */
    public static Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        return authentication.getAuthorities();
    }
    
    /**
     * 获取当前用户的权限字符串集合
     *
     * @return 权限字符串集合
     */
    public static Set<String> getAuthorities() {
        Collection<? extends GrantedAuthority> authorities = getGrantedAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptySet();
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
    
    /**
     * 获取当前用户的角色字符串集合
     *
     * @return 角色字符串集合
     */
    public static Set<String> getRoles() {
        Set<String> authorities = getAuthorities();
        if (authorities.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(authorities);
    }
    
    /**
     * 判断当前用户是否具有指定权限
     *
     * @param permission 权限标识
     * @return 是否具有权限
     */
    public static boolean hasPermission(String permission) {
        return getAuthorities().contains(permission);
    }
    
    /**
     * 判断当前用户是否具有指定角色
     *
     * @param role 角色标识
     * @return 是否具有角色
     */
    public static boolean hasRole(String role) {
        return getAuthorities().contains(role);
    }
}