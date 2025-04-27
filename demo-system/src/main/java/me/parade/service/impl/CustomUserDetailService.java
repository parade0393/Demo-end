package me.parade.service.impl;

import lombok.RequiredArgsConstructor;
import me.parade.domain.entity.SysUser;
import me.parade.service.MenuService;
import me.parade.service.RoleService;
import me.parade.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 获取用户角色和权限
        List<String> roleCodes = roleService.getUserRoleCodes(user.getId());
        List<String> permissions = menuService.getUserPermissions(user.getId());
        
        // 创建授权列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色
        roleCodes.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        
        // 添加权限
        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

        return User
                .withUsername(username)
                .password(user.getPassword())
                .accountLocked(user.getStatus() == 0)
                .authorities(authorities)
                .build();
    }
}
