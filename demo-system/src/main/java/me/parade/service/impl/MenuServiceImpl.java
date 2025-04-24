package me.parade.service.impl;

import lombok.RequiredArgsConstructor;
import me.parade.domain.entity.SysMenu;
import me.parade.domain.vo.MenuVO;
import me.parade.mapper.MenuMapper;
import me.parade.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<SysMenu> getUserMenuList(Long userId) {
        // 1. 如果是超级管理员，返回所有菜单
        if (isAdmin(userId)) {
            return menuMapper.selectAllMenus();
        }

        // 2. 否则，查询用户角色对应的菜单
        return menuMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<MenuVO> getUserMenuRoutes(Long userId) {
        // 1. 获取用户菜单列表
        List<SysMenu> menus = getUserMenuList(userId);

        // 2. 构建菜单树
        return buildMenuTree(menus);
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        // 1. 如果是超级管理员，返回所有权限标识
        if (isAdmin(userId)) {
            return menuMapper.selectAllPerms();
        }

        // 2. 否则，查询用户角色对应的权限标识
        return menuMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<MenuVO> buildMenuTree(List<SysMenu> menus) {
        // 1. 过滤出目录和菜单（排除按钮）
        List<SysMenu> filteredMenus = menus.stream()
                .filter(menu -> 1 == (menu.getType()) || 2 == (menu.getType()) || 4 == (menu.getType()))
                .collect(Collectors.toList());

        // 2. 构建菜单树
        List<MenuVO> menuTree = new ArrayList<>();
        for (SysMenu menu : filteredMenus) {
            if (menu.getParentId() == 0) {
                MenuVO menuVO = convertToMenuVO(menu);
                menuVO.setChildren(getChildren(menu, filteredMenus));
                menuTree.add(menuVO);
            }
        }

        // 3. 按orderNum排序
        menuTree.sort(Comparator.comparingInt(MenuVO::getOrderNum));
        return menuTree;
    }

    @Override
    public List<SysMenu> getChildrenMenus(Long parentId) {
        return menuMapper.selectByParentId(parentId);
    }

    /**
     * 判断是否是超级管理员
     */
    private boolean isAdmin(Long userId) {
        // TODO: 实现判断逻辑
        return userId == 1L;
    }

    /**
     * 获取子菜单
     */
    private List<MenuVO> getChildren(SysMenu parent, List<SysMenu> menus) {
        List<MenuVO> children = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parent.getId())) {
                MenuVO menuVO = convertToMenuVO(menu);
                menuVO.setChildren(getChildren(menu, menus));
                children.add(menuVO);
            }
        }
        children.sort(Comparator.comparingInt(MenuVO::getOrderNum));
        return children;
    }

    /**
     * 转换菜单实体为VO
     */
    private MenuVO convertToMenuVO(SysMenu menu) {
        return MenuVO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .path(menu.getRoutePath())
                .component(menu.getComponent())
                .orderNum(menu.getSort())
                .build();
    }
}