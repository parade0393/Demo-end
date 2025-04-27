package me.parade.controller.example;

import lombok.RequiredArgsConstructor;
import me.parade.result.Result;
import me.parade.security.annotation.RequiresPermission;
import me.parade.security.annotation.RequiresRoles;
import me.parade.security.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限示例控制器
 * <p>
 * 用于演示如何使用权限注解和权限服务进行接口权限控制
 */
@RestController
@RequestMapping("/example/permission")
@RequiredArgsConstructor
public class PermissionExampleController {

    private final PermissionService permissionService;

    /**
     * 需要user:list权限才能访问
     */
    @GetMapping("/users")
    @RequiresPermission("user:list")
    public Result<String> listUsers() {
        return Result.success("获取用户列表成功");
    }

    /**
     * 需要user:add权限才能访问
     */
    @GetMapping("/users/add")
    @RequiresPermission("user:add")
    public Result<String> addUser() {
        return Result.success("添加用户成功");
    }

    /**
     * 需要admin角色才能访问
     */
    @GetMapping("/admin")
    @RequiresRoles("admin")
    public Result<String> adminOperation() {
        return Result.success("管理员操作成功");
    }

    /**
     * 需要admin或operator角色才能访问
     */
    @GetMapping("/manage")
    @RequiresRoles(value = "admin,operator", logical = RequiresRoles.Logical.OR)
    public Result<String> manageOperation() {
        return Result.success("管理操作成功");
    }

    /**
     * 使用PermissionService进行手动权限检查
     */
    @GetMapping("/check")
    public Result<String> checkPermission(String permission) {
        boolean hasPermission = permissionService.hasPermission(permission);
        if (hasPermission) {
            return Result.success("有权限访问");
        } else {
            return Result.error("没有权限访问");
        }
    }

    /**
     * 使用PermissionService进行手动角色检查
     */
    @GetMapping("/check-role")
    public Result<String> checkRole(String role) {
        boolean hasRole = permissionService.hasRole(role);
        if (hasRole) {
            return Result.success("有角色访问");
        } else {
            return Result.error("没有角色访问");
        }
    }
}