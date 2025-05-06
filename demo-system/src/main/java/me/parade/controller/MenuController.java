package me.parade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.parade.annotation.ResponseResult;
import me.parade.domain.dto.menu.MenuRequest;
import me.parade.domain.entity.SysMenu;
import me.parade.security.annotation.RequiresPermission;
import me.parade.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 * 提供菜单管理相关接口
 */
@RestController
@RequestMapping("/menu")
@Tag(name = "菜单接口", description = "提供菜单管理相关接口")
@RequiredArgsConstructor
@ResponseResult
public class MenuController {

    private final MenuService menuService;

    /**
     * 获取菜单列表
     *
     * @return 菜单列表
     */
    @Operation(summary = "获取菜单列表", description = "获取所有菜单列表")
    @GetMapping("/list")
    @RequiresPermission("system:menu:query")
    public List<SysMenu> getMenuList() {
        return menuService.getMenuList();
    }

    /**
     * 获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    @Operation(summary = "获取菜单详情", description = "根据菜单ID获取菜单详情")
    @GetMapping("/{menuId}")
    @RequiresPermission("system:menu:query")
    public SysMenu getMenuDetail(@Parameter(description = "菜单ID") @PathVariable Long menuId) {
        return menuService.getMenuById(menuId);
    }

    /**
     * 创建菜单
     *
     * @param menuRequest 菜单请求参数
     * @return 创建的菜单ID
     */
    @Operation(summary = "创建菜单", description = "创建新菜单")
    @PostMapping
    @RequiresPermission("system:menu:add")
    public Long createMenu(@Parameter(description = "菜单参数") @RequestBody @Valid MenuRequest menuRequest) {
        return menuService.createMenu(menuRequest);
    }

    /**
     * 更新菜单
     *
     * @param menuRequest 菜单请求参数
     * @return 是否成功
     */
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    @PutMapping
    @RequiresPermission("system:menu:edit")
    public boolean updateMenu(@Parameter(description = "菜单参数") @RequestBody @Valid MenuRequest menuRequest) {
        return menuService.updateMenu(menuRequest);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单")
    @DeleteMapping("/{menuId}")
    @RequiresPermission("system:menu:delete")
    public boolean deleteMenu(@Parameter(description = "菜单ID") @PathVariable Long menuId) {
        return menuService.deleteMenu(menuId);
    }
}
