package me.parade.service;

import me.parade.domain.entity.SysMenu;
import me.parade.domain.vo.MenuVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务接口
 */
public interface MenuService {

    /**
     * 获取用户菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getUserMenuList(Long userId);

    /**
     * 获取用户菜单路由
     *
     * @param userId 用户ID
     * @return 路由列表
     */
    List<MenuVO> getUserMenuRoutes(Long userId);

    /**
     * 获取用户权限标识
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 构建菜单树结构
     *
     * @param menus 菜单列表
     * @return 树形菜单列表
     */
    List<MenuVO> buildMenuTree(List<SysMenu> menus);

    /**
     * 根据父节点ID获取子菜单
     *
     * @param parentId 父节点ID
     * @return 子菜单列表
     */
    List<SysMenu> getChildrenMenus(Long parentId);
}