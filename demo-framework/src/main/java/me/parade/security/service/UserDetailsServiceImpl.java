package me.parade.security.service;


import lombok.RequiredArgsConstructor;
import me.parade.domain.entity.SysUser;
import me.parade.domain.dto.LoginUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义UserDetailsService实现
 * 用于加载用户信息并创建UserDetails对象
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // TODO: 注入UserMapper，等待后续实现
    // private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 从数据库加载用户信息，这里先模拟一个用户
        if ("admin".equals(username)) {
            SysUser user = new SysUser();
            user.setId(1L);
            user.setUsername("admin");
            // 密码为admin123的BCrypt加密结果
            user.setPassword("$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2");
            user.setStatus((byte) 0); // 0-正常 1-停用

            // 创建LoginUser对象
            return createLoginUser(user);
        }

        throw new UsernameNotFoundException("用户名或密码错误");
    }

    /**
     * 创建LoginUser对象
     *
     * @param user 用户信息
     * @return LoginUser
     */
    private LoginUser createLoginUser(SysUser user) {

        // TODO: 查询用户权限信息，等待后续实现
        // Set<String> permissions = permissionService.getPermissionsByUserId(user.getId());

        return new LoginUser(user/*, permissions*/);
    }
}