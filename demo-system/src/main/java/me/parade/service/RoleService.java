package me.parade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.parade.domain.dto.role.RoleQueryRequest;
import me.parade.domain.entity.SysRole;
import me.parade.domain.entity.SysUser;

import java.util.List;

/**
 * 角色服务接口
 * 提供角色查询、用户角色关联等功能
 */
public interface RoleService {

    /**
     * 获取用户角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    List<String> getUserRoleCodes(Long userId);

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 分页查询角色列表
     *
     * @param page         分页参数
     * @param queryRequest 查询条件
     * @return 角色分页列表
     */
    IPage<SysRole> getRolePage(Page<SysRole> page, RoleQueryRequest queryRequest);
    
    /**
     * 查询角色列表（不分页）
     *
     * @param queryRequest 查询条件
     * @return 角色列表
     */
    List<SysRole> getRoleList(RoleQueryRequest queryRequest);

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    SysRole getRoleById(Long roleId);

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean addRole(SysRole role);

    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(SysRole role);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long roleId);

    /**
     * 获取角色菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 分配角色菜单权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     * @return 是否成功
     */
    boolean assignRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色用户列表
     *
     * @param page     分页参数
     * @param roleId   角色ID
     * @param username 用户名（可选，模糊查询）
     * @return 用户分页列表
     */
    IPage<SysUser> getRoleUserPage(Page<SysUser> page, Long roleId, String username);

    /**
     * 取消用户角色
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelUserRole(Long roleId, Long userId);
}