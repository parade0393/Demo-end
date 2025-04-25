package me.parade.service.impl;

import lombok.RequiredArgsConstructor;
import me.parade.domain.dto.menu.MenuTreeVO;
import me.parade.domain.dto.menu.Meta;
import me.parade.domain.entity.SysMenu;
import me.parade.mapper.MenuMapper;
import me.parade.security.utils.SecurityUtils;
import me.parade.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<MenuTreeVO> getCurrentUserMenuTree() {
        // 获取当前认证用户名
        String username = SecurityUtils.getUsername();
        
        // 根据用户名查询用户ID
        Long userId = menuMapper.selectUserIdByUsername(username);
        
        return getUserMenuTree(userId);
    }

    @Override
    public List<MenuTreeVO> getUserMenuTree(Long userId) {
        // 查询用户菜单列表
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        
        // 转换为树形结构
        return buildMenuTree(menus);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // 查询用户权限列表
        return menuMapper.selectPermsByUserId(userId);
    }

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    private List<MenuTreeVO> buildMenuTree(List<SysMenu> menus) {
        // 转换为MenuTreeVO
        List<MenuTreeVO> menuVOs = menus.stream()
                .map(this::convertToMenuTreeVO)
                .toList();

        // 按父ID分组
        Map<Long, List<MenuTreeVO>> parentIdMap = menuVOs.stream()
                .collect(Collectors.groupingBy(MenuTreeVO::getParentId));

        // 构建树形结构
        menuVOs.forEach(menu -> {
            List<MenuTreeVO> children = parentIdMap.get(menu.getId());
            if (children != null) {
                // 对子菜单排序
                children.sort(Comparator.comparing(MenuTreeVO::getSort));
                menu.setChildren(children);
            }
        });

        // 返回顶层菜单（parentId为0或null的菜单）
        List<MenuTreeVO> rootMenus = menuVOs.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .sorted(Comparator.comparing(MenuTreeVO::getSort))
                .collect(Collectors.toList());

        return rootMenus;
    }

    /**
     * 将SysMenu转换为MenuTreeVO
     *
     * @param menu 菜单实体
     * @return 菜单VO
     */
    private MenuTreeVO convertToMenuTreeVO(SysMenu menu) {
        // 构建Meta对象
        Meta meta = Meta.builder()
                .title(menu.getName())
                .icon(menu.getIcon())
                .hidden(menu.getVisible() == 0)
                .keepAlive(true)
                .build();
                
        return MenuTreeVO.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .name(menu.getName())
                .icon(menu.getIcon())
                .routeName(menu.getRouteName())
                .routePath(menu.getRoutePath())
                .component(menu.getComponent())
                .sort(menu.getSort())
                .hidden(menu.getVisible() == 0)
                .meta(meta)
                .children(new ArrayList<>())
                .build();
    }
}