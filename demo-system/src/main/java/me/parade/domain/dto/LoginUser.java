package me.parade.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.parade.domain.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户身份权限
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户信息
     */
    private SysUser user;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    private Set<String> roles;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    private String token;

    public LoginUser(SysUser user) {
        this.user = user;
        this.userId = user.getId();
        this.username = user.getUsername();
        this.permissions = Collections.emptySet(); // 初始化为空集合，等待后续权限实现
    }

    public LoginUser(SysUser user, Set<String> permissions) {
        this.user = user;
        this.userId = user.getId();
        this.username = user.getUsername();
        this.permissions = permissions;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将permissions转换为GrantedAuthority集合
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 账户是否未过期，过期无法验证
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 指定用户是否解锁，锁定的用户无法进行身份验证
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 指示是否已过期的用户的凭据(密码)，过期的凭据防止认证
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用，禁用的用户不能身份验证
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return user.getStatus() == 0;
    }
}