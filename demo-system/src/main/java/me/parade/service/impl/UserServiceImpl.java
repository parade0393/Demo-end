package me.parade.service.impl;


import lombok.RequiredArgsConstructor;
import me.parade.domain.entity.SysUser;
import me.parade.domain.vo.MenuVO;
import me.parade.domain.vo.UserInfoVO;
import me.parade.mapper.UserMapper;
import me.parade.service.MenuService;
import me.parade.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MenuService menuService;

    @Override
    public SysUser getUserByUsername(String username) {
        // TODO: 实现根据用户名查询用户
        // return userMapper.selectByUsername(username);
        return null;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        // 1. 获取用户基本信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        // 2. 获取用户角色
        Set<String> roles = getUserRoles(userId);

        // 3. 获取用户权限
        Set<String> permissions = getUserPermissions(userId);

        // 4. 获取用户菜单路由
        List<MenuVO> routes = menuService.getUserMenuRoutes(userId);

        // 5. 构建并返回用户信息
        return UserInfoVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roles(List.copyOf(roles))
                .permissions(permissions)
                .routes(routes)
                .build();
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        // TODO: 实现获取用户权限列表
        // 1. 如果是超级管理员，返回所有权限
        // 2. 否则，查询用户角色对应的权限
        return Collections.emptySet();
    }

    @Override
    public Set<String> getUserRoles(Long userId) {
        // TODO: 实现获取用户角色列表
        // 从用户角色关联表中查询
        return Collections.emptySet();
    }
}