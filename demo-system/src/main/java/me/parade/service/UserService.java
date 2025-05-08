package me.parade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.parade.domain.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param page 分页参数
     * @param user 查询条件
     * @return 用户分页列表
     */
    IPage<SysUser> getUserPage(Page<SysUser> page, SysUser user);

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean addUser(SysUser user, List<Long> roleIds);

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUser user);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态(0:禁用,1:正常)
     * @return 是否成功
     */
    boolean changeStatus(Long userId, Integer status);

    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getById(Long userId);
    
    /**
     * 根据部门ID列表分页查询用户列表
     *
     * @param page 分页参数
     * @param user 查询条件
     * @param deptIds 部门ID列表
     * @return 用户分页列表
     */
    IPage<SysUser> getUserPageByDeptIds(Page<SysUser> page, SysUser user, List<Long> deptIds);
}