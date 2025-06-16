package me.parade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import me.parade.domain.dto.menu.MenuRequest;
import me.parade.domain.dto.menu.MenuTreeVO;
import me.parade.domain.dto.menu.Meta;
import me.parade.domain.entity.SysMenu;
import me.parade.exception.BusinessException;
import me.parade.mapper.MenuMapper;
import me.parade.security.utils.SecurityUtils;
import me.parade.service.MenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        List<SysMenu> menus;
        if(SecurityUtils.isAdmin()){
            //超级管理员
            menus = menuMapper.selectAllVisibleMenus();
        }else{
            // 查询用户菜单列表
            menus = menuMapper.selectMenusByUserId(userId);
        }
        
        // 转换为树形结构
        return buildMenuTree(menus);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // 查询用户权限列表
        return menuMapper.selectPermsByUserId(userId);
    }
    
    @Override
    public List<SysMenu> getMenuList() {
        // 获取当前认证用户名
        String username = SecurityUtils.getUsername();
        
        // 根据用户名查询用户ID
        Long userId = menuMapper.selectUserIdByUsername(username);
        List<SysMenu> menus;
        if(SecurityUtils.isAdmin()){
            //超级管理员
            menus = menuMapper.selectAllVisibleMenus();
        }else{
            // 查询用户菜单列表
            menus = menuMapper.selectMenusByUserId(userId);
        }
        

        
        // 构建树形结构并返回
        return buildSysMenuTree(menus);
    }
    
    /**
     * 构建SysMenu树形结构
     *
     * @param menus 菜单列表
     * @return 菜单树形结构
     */
    private List<SysMenu> buildSysMenuTree(List<SysMenu> menus) {
        // 按父ID分组
        Map<Long, List<SysMenu>> parentIdMap = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 构建树形结构
        menus.forEach(menu -> {
            List<SysMenu> children = parentIdMap.get(menu.getId());
            if (children != null) {
                // 对子菜单排序
                children.sort(Comparator.comparing(SysMenu::getSort));
                menu.setChildren(children);
            }
        });

        // 返回顶层菜单（parentId为0或null的菜单）
        return menus.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .sorted(Comparator.comparing(SysMenu::getSort))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SysMenu> getAllMenuTree() {
        // 查询所有可见菜单
        List<SysMenu> menus = menuMapper.selectAllVisibleMenus();
        
        // 构建树形结构
        return buildSysMenuTree(menus);
    }
    
    @Override
    public SysMenu getMenuById(Long menuId) {
        return menuMapper.selectById(menuId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuRequest menuRequest) {
//        if(menuRequest.getType() == 1 && menuRequest.getComponent() ==null){
//            menuRequest.setComponent("Layout");
//        }
        // 转换为实体
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(menuRequest, menu);
        
        // 设置树路径
        if (menuRequest.getParentId() != null && menuRequest.getParentId() > 0) {
            SysMenu parentMenu = menuMapper.selectById(menuRequest.getParentId());
            if (parentMenu == null) {
                throw new BusinessException("父菜单不存在");
            }
            // 设置树路径，格式：父ID,祖父ID,...
            String treePath = parentMenu.getTreePath() != null ? 
                    parentMenu.getTreePath() + "," + parentMenu.getId() : 
                    parentMenu.getId().toString();
            menu.setTreePath(treePath);
        } else {
            // 顶级菜单
            menu.setParentId(0L);
            menu.setTreePath("0");
        }
        
        // 插入数据库
        menuMapper.insert(menu);
        return menu.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(MenuRequest menuRequest) {
        // 检查菜单是否存在
        if (menuRequest.getId() == null) {
            throw new BusinessException("菜单ID不能为空");
        }
        SysMenu existMenu = menuMapper.selectById(menuRequest.getId());
        if (existMenu == null) {
            throw new BusinessException("菜单不存在");
        }
        
        // 转换为实体
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(menuRequest, menu);
        
        // 更新树路径（如果父节点变化）
        if (!existMenu.getParentId().equals(menu.getParentId())) {
            // 父节点发生变化，需要更新树路径
            if (menu.getParentId() != null && menu.getParentId() > 0) {
                SysMenu parentMenu = menuMapper.selectById(menu.getParentId());
                if (parentMenu == null) {
                    throw new BusinessException("父菜单不存在");
                }
                // 设置树路径，格式：父ID,祖父ID,...
                String treePath = parentMenu.getTreePath() != null ? 
                        parentMenu.getTreePath() + "," + parentMenu.getId() : 
                        parentMenu.getId().toString();
                menu.setTreePath(treePath);
            } else {
                // 顶级菜单
                menu.setParentId(0L);
                menu.setTreePath("0");
            }
        } else {
            // 父节点没变，保持原树路径
            menu.setTreePath(existMenu.getTreePath());
        }
        
        // 更新数据库
        return menuMapper.updateById(menu) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Long menuId) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = Wrappers.lambdaQuery(SysMenu.class)
                .eq(SysMenu::getParentId, menuId);
        long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("存在子菜单，无法删除");
        }
        
        // 删除菜单
        return menuMapper.deleteById(menuId) > 0;
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
                .filter(menu -> menu.getType() != 3)
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
                .alwaysShow(menu.getAlwaysShow() == 1)
                .keepAlive(menu.getKeepAlive() == 1)
                .query(menu.getQuery())
                .build();
        return MenuTreeVO.builder()
                .id(menu.getId())
                .parentId(menu.getParentId())
                .name(menu.getRouteName())
                .path(menu.getRoutePath())
                .redirect(menu.getRedirect())
                .component(menu.getComponent())
                .sort(menu.getSort())
                .meta(meta)
                .children(new ArrayList<>())
                .build();
    }
}