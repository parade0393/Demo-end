package me.parade.service.impl;

import lombok.RequiredArgsConstructor;
import me.parade.mapper.RoleMapper;
import me.parade.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<String> getUserRoleCodes(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return roleMapper.selectRoleIdsByUserId(userId);
    }
}