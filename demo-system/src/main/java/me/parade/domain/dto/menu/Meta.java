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
}