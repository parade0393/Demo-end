package me.parade.service;

import me.parade.domain.dto.menu.MenuRequest;
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
    
    /**
     * 获取当前登录用户的菜单列表
     * 基于RBAC权限模型，直接返回当前用户有权限访问的所有菜单
     * 
     * @return 当前用户可访问的菜单列表
     */
    List<SysMenu> getMenuList();
    
    /**
     * 根据ID获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    SysMenu getMenuById(Long menuId);
    
    /**
     * 创建菜单
     *
     * @param menuRequest 菜单请求参数
     * @return 创建的菜单ID
     */
    Long createMenu(MenuRequest menuRequest);
    
    /**
     * 更新菜单
     *
     * @param menuRequest 菜单请求参数
     * @return 是否成功
     */
    boolean updateMenu(MenuRequest menuRequest);
    
    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    boolean deleteMenu(Long menuId);
}