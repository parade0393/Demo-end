package me.parade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.parade.domain.dto.role.RoleQueryRequest;
import me.parade.domain.entity.SysRole;
import me.parade.domain.entity.SysRoleMenu;
import me.parade.domain.entity.SysUser;
import me.parade.domain.entity.SysUserRole;
import me.parade.mapper.MenuMapper;
import me.parade.mapper.RoleMapper;
import me.parade.mapper.RoleMenuMapper;
import me.parade.mapper.UserRoleMapper;
import me.parade.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, SysRole> implements RoleService {

    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    public List<String> getUserRoleCodes(Long userId) {
        return roleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return roleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public IPage<SysRole> getRolePage(Page<SysRole> page, RoleQueryRequest queryRequest) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (queryRequest != null) {
            // 按角色名称模糊查询
            if (StringUtils.hasText(queryRequest.getName())) {
                queryWrapper.like(SysRole::getName, queryRequest.getName());
            }

            // 按角色编码模糊查询
            if (StringUtils.hasText(queryRequest.getCode())) {
                queryWrapper.like(SysRole::getCode, queryRequest.getCode());
            }

            // 按状态查询
            if (queryRequest.getStatus() != null) {
                queryWrapper.eq(SysRole::getStatus, queryRequest.getStatus());
            }
        }

        // 只查询未删除的角色
        queryWrapper.eq(SysRole::getIsDeleted, 0);

        // 按排序字段升序排序
        queryWrapper.orderByAsc(SysRole::getSort);

        return roleMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public List<SysRole> getRoleList(RoleQueryRequest queryRequest) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if (queryRequest != null) {
            // 按角色名称模糊查询
            if (StringUtils.hasText(queryRequest.getName())) {
                queryWrapper.like(SysRole::getName, queryRequest.getName());
            }

            // 按角色编码模糊查询
            if (StringUtils.hasText(queryRequest.getCode())) {
                queryWrapper.like(SysRole::getCode, queryRequest.getCode());
            }

            // 按状态查询
            if (queryRequest.getStatus() != null) {
                queryWrapper.eq(SysRole::getStatus, queryRequest.getStatus());
            }
        }

        // 只查询未删除的角色
        queryWrapper.eq(SysRole::getIsDeleted, 0);

        // 按排序字段升序排序
        queryWrapper.orderByAsc(SysRole::getSort);

        return roleMapper.selectList(queryWrapper);
    }

    @Override
    public SysRole getRoleById(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(SysRole role) {
        // 设置默认值
        if (role.getStatus() == null) {
            role.setStatus((byte) 1); // 默认启用
        }
        if (role.getIsDeleted() == null) {
            role.setIsDeleted((byte) 0); // 默认未删除
        }

        return roleMapper.insert(role) > 0;
    }

    @Override
    public boolean updateRole(SysRole role) {
        return roleMapper.updateById(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // 逻辑删除角色
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setIsDeleted((byte) 1);

        // 删除角色菜单关联
        LambdaQueryWrapper<SysRoleMenu> menuQueryWrapper = new LambdaQueryWrapper<>();
        menuQueryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(menuQueryWrapper);

        // 删除角色用户关联
        LambdaQueryWrapper<SysUserRole> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(SysUserRole::getRoleId, roleId);
        userRoleMapper.delete(userQueryWrapper);

        return roleMapper.updateById(role) > 0;
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return menuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有的菜单关联
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(queryWrapper);

        // 添加新的菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenus = new ArrayList<>();
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenus.add(roleMenu);
            }

            // 批量插入角色菜单关联
            for (SysRoleMenu roleMenu : roleMenus) {
                roleMenuMapper.insert(roleMenu);
            }
        }

        return true;
    }

    @Override
    public IPage<SysUser> getRoleUserPage(Page<SysUser> page, Long roleId, String username) {
        return page.setRecords(userRoleMapper.selectUsersByRoleId(roleId, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelUserRole(Long roleId, Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getRoleId, roleId);
        queryWrapper.eq(SysUserRole::getUserId, userId);
        return userRoleMapper.delete(queryWrapper) > 0;
    }
    
    @Override
    public String getRoleNameById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        SysRole role = roleMapper.selectById(roleId);
        return role != null ? role.getName() : null;
    }
}