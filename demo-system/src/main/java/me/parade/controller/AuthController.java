package me.parade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.parade.annotation.ResponseResult;
import me.parade.domain.dto.auth.LoginRequest;
import me.parade.domain.dto.auth.LoginResponse;
import me.parade.domain.dto.auth.UserInfoResponse;
import me.parade.domain.dto.menu.MenuTreeVO;
import me.parade.service.AuthService;
import me.parade.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 * 提供登录、获取用户信息、获取菜单路由等接口
 */
@Tag(name = "认证接口", description = "提供登录、获取用户信息、获取菜单路由等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@ResponseResult
public class AuthController {

    private final AuthService authService;
    private final MenuService menuService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应（包含token、刷新token和过期时间）
     */
    @Operation(summary = "用户登录", description = "用户登录接口，用于用户登录认证")
    @PostMapping("/login")
    public LoginResponse login(@Parameter(description = "登录参数") @RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息（包含基本信息、权限和角色）
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前用户信息，包含基本信息、权限和角色")
    @GetMapping("/user/info")
    public UserInfoResponse getUserInfo() {
        return authService.getCurrentUserInfo();
    }

    /**
     * 获取当前用户的菜单路由
     *
     * @return 菜单树形结构
     */
    @Operation(summary = "获取当前用户的菜单路由", description = "获取当前用户的菜单路由，包含菜单树形结构")
    @GetMapping("/user/route")
    public List<MenuTreeVO> getUserRoutes() {
        return menuService.getCurrentUserMenuTree();
    }
    
    /**
     * 用户注销
     * 清除当前用户的认证信息
     */
    @Operation(summary = "用户注销", description = "用户注销接口，用于用户注销认证")
    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }
}