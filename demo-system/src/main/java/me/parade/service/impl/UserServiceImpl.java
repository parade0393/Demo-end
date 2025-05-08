package me.parade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.parade.domain.entity.SysUser;
import me.parade.domain.entity.SysUserRole;
import me.parade.mapper.RoleMapper;
import me.parade.mapper.UserMapper;
import me.parade.mapper.UserRoleMapper;
import me.parade.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SysUser getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public IPage<SysUser> getUserPage(Page<SysUser> page, SysUser user) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (user != null) {
            // 按用户名模糊查询
            if (StringUtils.hasText(user.getUsername())) {
                queryWrapper.like(SysUser::getUsername, user.getUsername());
            }
            
//            // 按昵称模糊查询
//            if (StringUtils.hasText(user.getNickname())) {
//                queryWrapper.like(SysUser::getNickname, user.getNickname());
//            }
            
            // 按手机号模糊查询
            if (StringUtils.hasText(user.getPhone())) {
                queryWrapper.like(SysUser::getPhone, user.getPhone());
            }
            
            // 按状态查询
            if (user.getStatus() != null) {
                queryWrapper.eq(SysUser::getStatus, user.getStatus());
            }
            if(user.getDeptId() != null) {
                queryWrapper.eq(SysUser::getDeptId, user.getDeptId());
            }
        }
        
        // 只查询未删除的用户
        queryWrapper.eq(SysUser::getIsDeleted, 0);
        
        // 按创建时间降序排序
        queryWrapper.orderByAsc(SysUser::getId);
        
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(SysUser user, List<Long> roleIds) {
        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 保存用户信息
        int rows = userMapper.insert(user);
        
        // 分配用户角色
        if (rows > 0 && roleIds != null && !roleIds.isEmpty()) {
            assignUserRoles(user.getId(), roleIds);
        }
        
        return rows > 0;
    }

    @Override
    public boolean updateUser(SysUser user) {
        // 如果密码不为空，则进行加密
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 不更新密码字段
            user.setPassword(null);
        }
        
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        // 逻辑删除用户
        SysUser user = new SysUser();
        user.setId(userId);
        user.setIsDeleted((byte) 1);
        
        // 删除用户角色关联
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(queryWrapper);
        
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean changeStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus((byte) status.intValue());
        
        return userMapper.updateById(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignUserRoles(Long userId, List<Long> roleIds) {
        // 先删除原有的角色关联
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        userRoleMapper.delete(queryWrapper);
        
        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }
            
            // 批量插入用户角色关联
            for (SysUserRole userRole : userRoles) {
                userRoleMapper.insert(userRole);
            }
        }
        
        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return roleMapper.selectRoleIdsByUserId(userId);
    }
    
    @Override
    public SysUser getById(Long userId) {
        return super.getById(userId);
    }
    
    @Override
    public IPage<SysUser> getUserPageByDeptIds(Page<SysUser> page, SysUser user, List<Long> deptIds) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (user != null) {
            // 按用户名模糊查询
            if (StringUtils.hasText(user.getUsername())) {
                queryWrapper.like(SysUser::getUsername, user.getUsername());
            }
            
            // 按姓名模糊查询
            if (StringUtils.hasText(user.getName())) {
                queryWrapper.like(SysUser::getName, user.getName());
            }
            
            // 按手机号模糊查询
            if (StringUtils.hasText(user.getPhone())) {
                queryWrapper.like(SysUser::getPhone, user.getPhone());
            }
            
            // 按状态查询
            if (user.getStatus() != null) {
                queryWrapper.eq(SysUser::getStatus, user.getStatus());
            }
        }
        
        // 添加部门ID条件（使用IN查询）
        if (deptIds != null && !deptIds.isEmpty()) {
            queryWrapper.in(SysUser::getDeptId, deptIds);
        }
        
        // 只查询未删除的用户
        queryWrapper.eq(SysUser::getIsDeleted, 0);
        
        // 按创建时间降序排序
        queryWrapper.orderByAsc(SysUser::getId);
        
        return this.page(page, queryWrapper);
    }
}