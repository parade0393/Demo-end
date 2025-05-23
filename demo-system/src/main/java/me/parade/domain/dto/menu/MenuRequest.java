package me.parade.domain.dto.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单请求DTO
 * <p>
 * 用于创建和更新菜单
 */
@Data
@Schema(description = "菜单请求参数")
public class MenuRequest {
    
    /**
     * 菜单ID（更新时必填）
     */
    @Schema(description = "菜单ID（更新时必填）")
    private Long id;
    
    /**
     * 父节点ID
     */
    @Schema(description = "父节点ID")
    private Long parentId;
    
    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名称", required = true)
    private String name;
    
    /**
     * 菜单类型（1-目录 2-菜单 3-按钮 4-外链）
     */
    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "菜单类型（1-目录 2-菜单 3-按钮 4-外链）", required = true)
    private Byte type;
    
    /**
     * 路由名称（Vue Router 命名路由名称）
     */
    @Schema(description = "路由名称")
    private String routeName;
    
    /**
     * 路由路径（Vue Router 中定义的 URL 路径）
     */
    @Schema(description = "路由路径")
    private String routePath;
    
    /**
     * 组件路径（相对于src/views/，或者后缀.vue）
     */
    @Schema(description = "组件路径")
    private String component;
    
    /**
     * [按钮] 权限标识
     */
    @Schema(description = "权限标识")
    private String perm;
    
    /**
     * [目录] 是否一个路由显示（1-是 0-否）
     */
    @Schema(description = "是否总是显示")
    private Byte alwaysShow;
    
    /**
     * [菜单] 是否开启页面缓存（1-是 0-否）
     */
    @Schema(description = "是否开启页面缓存")
    private Byte keepAlive;
    
    /**
     * 显示状态（1-显示 0-隐藏）
     */
    @Schema(description = "显示状态（1-显示 0-隐藏）")
    private Byte visible;
    
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
    
    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标")
    private String icon;
    /**
     * 重定向地址
     */
    @Schema(description = "重定向地址")
    private String redirect;
}