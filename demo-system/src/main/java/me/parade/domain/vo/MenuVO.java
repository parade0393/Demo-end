package me.parade.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单路由视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVO {

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 菜单元数据
     */
    private MenuMetaVO meta;

    /**
     * 子菜单
     */
    private List<MenuVO> children;
}