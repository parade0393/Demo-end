package me.parade.domain.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单树形结构VO
 * <p>
 * 符合前端路由规范的菜单树形结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeVO {
    
    /**
     * 菜单ID
     */
    private Long id;
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单名称
     */
    private String name;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 路由名称
     */
    private String routeName;
    
    /**
     * 路由路径
     */
    private String routePath;
    
    /**
     * 组件路径
     */
    private String component;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 是否隐藏
     */
    private Boolean hidden;
    
    /**
     * 路由元数据
     */
    private Meta meta;
    
    /**
     * 子菜单
     */
    private List<MenuTreeVO> children;
}