package me.parade.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单元数据视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuMetaVO {

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 是否隐藏
     */
    private Boolean hidden;

    /**
     * 是否缓存
     */
    private Boolean noCache;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 外链地址
     */
    private String link;

    /**
     * 是否为外链
     */
    private Boolean isFrame;
}