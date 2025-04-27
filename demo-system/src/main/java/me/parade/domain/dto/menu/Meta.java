package me.parade.domain.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单元数据
 * <p>
 * 用于前端路由配置的元数据信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 是否缓存
     */
    private Boolean keepAlive;
    
    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * 是否总是显示,父级菜单设置true的时候，及时只有一个可见子菜单也会显示父级菜单，否则只显示唯一可见的子菜单
     */
    private Boolean alwaysShow;
}