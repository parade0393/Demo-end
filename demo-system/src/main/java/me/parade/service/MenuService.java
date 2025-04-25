package me.parade.service;

import me.parade.domain.dto.menu.MenuTreeVO;
import me.parade.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 * 提供菜单树查询、用户菜单权限等功能
 */
public interface MenuService {

    /**
     * 获取当前登录用户的菜单树
     *
     * @return 菜单树形结构
     */
    List<MenuTreeVO> getCurrentUserMenuTree();
    
    /**
     * 根据用户ID获取菜单树
     *
     * @param userId 用户ID
     * @return 菜单树形结构
     */
    List<MenuTreeVO> getUserMenuTree(Long userId);
    
    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissions(Long userId);
}