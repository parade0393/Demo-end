package me.parade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.parade.annotation.ResponseResult;
import me.parade.domain.dto.role.*;
import me.parade.domain.entity.SysRole;
import me.parade.domain.entity.SysUser;
import me.parade.domain.vo.UserVO;
import me.parade.security.annotation.RequiresPermission;
import me.parade.service.DeptService;
import me.parade.service.RoleService;
import me.parade.utils.UserConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色接口", description = "提供角色管理相关接口")
@RequiredArgsConstructor
@ResponseResult
public class RoleController {

    private final RoleService roleService;
    private final DeptService deptService;

    /**
     * 角色列表查询
     *
     * @param current 当前页
     * @param size 每页大小
     * @param queryRequest 查询条件
     * @return 角色分页列表
     */
    @Operation(summary = "角色列表查询", description = "分页查询角色列表，可根据名称和编码过滤")
    @GetMapping("/list")
    @RequiresPermission("system:role:query")
    public IPage<SysRole> getRoleList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "查询条件") RoleQueryRequest queryRequest) {
        Page<SysRole> page = new Page<>(current, size);
        return roleService.getRolePage(page, queryRequest);
    }
    
    /**
     * 角色列表查询（不分页）
     *
     * @param queryRequest 查询条件
     * @return 角色列表
     */
    @Operation(summary = "角色列表查询（不分页）", description = "查询所有角色列表，可根据名称和编码过滤")
    @GetMapping("/all")
    @RequiresPermission("system:role:query")
    public List<SysRole> getAllRoles(@Parameter(description = "查询条件") RoleQueryRequest queryRequest) {
        return roleService.getRoleList(queryRequest);
    }

    /**
     * 角色详情
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @Operation(summary = "角色详情", description = "根据角色ID获取角色详情")
    @GetMapping("/{roleId}")
    @RequiresPermission("system:role:query")
    public SysRole getRoleDetail(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        return roleService.getRoleById(roleId);
    }

    /**
     * 角色拥有的权限查询
     *
     * @param roleId 角色ID
     * @return 角色拥有的菜单ID列表
     */
    @Operation(summary = "角色拥有的权限查询", description = "获取角色拥有的菜单权限ID列表")
    @GetMapping("/{roleId}/menus")
    @RequiresPermission("system:role:query")
    public List<Long> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        return roleService.getRoleMenuIds(roleId);
    }



    /**
     * 角色用户查询
     *
     * @param current 当前页
     * @param size 每页大小
     * @param queryRequest 查询条件
     * @return 用户分页列表
     */
    @Operation(summary = "角色用户查询", description = "查询拥有指定角色的用户列表，可根据用户名过滤")
    @GetMapping("/users")
    @RequiresPermission("system:role:query")
    public IPage<UserVO> getRoleUsers(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "查询条件") RoleUserQueryRequest queryRequest) {
        Page<SysUser> page = new Page<>(current, size);
        IPage<SysUser> roleUserPage = roleService.getRoleUserPage(page, queryRequest.getRoleId(), queryRequest.getUsername());
        return roleUserPage.convert(user -> UserConverter.convertToUserVO(user, roleService, deptService));
    }

    /**
     * 新增角色
     *
     * @param roleRequest 角色信息
     * @return 是否成功
     */
    @Operation(summary = "新增角色", description = "新增角色信息")
    @PostMapping("/create")
    @RequiresPermission("system:role:add")
    public boolean createRole(@Parameter(description = "角色信息") @RequestBody @Valid RoleRequest roleRequest) {
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleRequest, role);
        return roleService.addRole(role);
    }

    /**
     * 修改角色
     *
     * @param roleRequest 角色信息
     * @return 是否成功
     */
    @Operation(summary = "修改角色", description = "修改角色信息")
    @PostMapping("/update")
    @RequiresPermission("system:role:edit")
    public boolean updateRole(@Parameter(description = "角色信息") @RequestBody @Valid RoleRequest roleRequest) {
        if (roleRequest.getId() == null) {
            return false;
        }
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleRequest, role);
        return roleService.updateRole(role);
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 是否成功
     */
    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @GetMapping("/delete")
    @RequiresPermission("system:role:delete")
    public boolean deleteRole(@Parameter(description = "角色ID") @RequestParam Long roleId) {
        return roleService.deleteRole(roleId);
    }

    /**
     * 角色权限分配
     *
     * @param roleMenuRequest 角色菜单权限分配请求
     * @return 是否成功
     */
    @Operation(summary = "角色权限分配", description = "为角色分配菜单权限")
    @PostMapping("/assign-menus")
    @RequiresPermission("system:role:edit")
    public boolean assignRoleMenus(@Parameter(description = "角色菜单权限分配请求") @RequestBody @Valid RoleMenuRequest roleMenuRequest) {
        return roleService.assignRoleMenus(roleMenuRequest.getRoleId(), roleMenuRequest.getMenuIds());
    }

    /**
     * 角色用户取消分配角色
     *
     * @param cancelRequest 取消分配请求
     * @return 是否成功
     */
    @Operation(summary = "角色用户取消分配角色", description = "取消用户的角色分配")
    @PostMapping("/cancel-user-role")
    @RequiresPermission("system:role:edit")
    public boolean cancelUserRole(@Parameter(description = "取消分配请求") @RequestBody @Valid RoleUserCancelRequest cancelRequest) {
        return roleService.cancelUserRole(cancelRequest.getRoleId(), cancelRequest.getUserId());
    }


}