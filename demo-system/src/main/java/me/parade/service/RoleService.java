package me.parade.service;

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
}