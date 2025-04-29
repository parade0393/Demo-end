package me.parade.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.parade.domain.common.BaseEntity;

import java.util.List;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author parade
 * @since 2025-04-23 16:37:44
 */
@Getter
@Setter
@ToString
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父节点ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 父节点路径
     */
    @TableField("tree_path")
    private String treePath;

    /**
     * 菜单名称
     */
    @TableField("name")
    private String name;

    /**
     * 菜单类型（1-目录 2-菜单 3-按钮 4-外链）
     */
    @TableField("type")
    private Byte type;

    /**
     * 路由名称（Vue Router 命名路由名称）
     */
    @TableField("route_name")
    private String routeName;

    /**
     * 路由路径（Vue Router 中定义的 URL 路径）
     */
    @TableField("route_path")
    private String routePath;

    /**
     * 组件路径（相对于src/views/，或者后缀.vue）
     */
    @TableField("component")
    private String component;

    /**
     * [按钮] 权限标识
     */
    @TableField("perm")
    private String perm;

    /**
     * [目录] 是否一个路由显示（1-是 0-否）
     */
    @TableField("always_show")
    private Byte alwaysShow;

    /**
     * [菜单] 是否开启页面缓存（1-是 0-否）
     */
    @TableField("keep_alive")
    private Byte keepAlive;

    /**
     * 显示状态（1-显示 0-隐藏）
     */
    @TableField("visible")
    private Byte visible;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 菜单图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 跳转路径
     */
    @TableField("redirect")
    private String redirect;

    /**
     * 路由参数
     */
    @TableField("params")
    private String params;

    /**
     * 子菜单列表
     */
    @TableField(exist = false)
    private List<SysMenu> children;

}
