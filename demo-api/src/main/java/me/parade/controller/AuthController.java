package me.parade.controller;

import lombok.RequiredArgsConstructor;
import me.parade.domain.dto.LoginDTO;
import me.parade.domain.vo.LoginVO;
import me.parade.domain.vo.MenuVO;
import me.parade.domain.vo.UserInfoVO;
import me.parade.result.Result;
import me.parade.result.ResultCode;
import me.parade.domain.dto.LoginUser;
import me.parade.security.service.LoginService;
import me.parade.security.utils.SecurityUtils;
import me.parade.service.MenuService;
import me.parade.service.PermissionService;
import me.parade.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final UserService userService;
    private final MenuService menuService;
    private final PermissionService permissionService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        // 调用登录服务
        LoginUser loginUser = loginService.login(loginDTO.getUsername(), loginDTO.getPassword());

        // 构建响应数据
        LoginVO loginVO = LoginVO.builder()
                .token(loginUser.getToken())
                .expireTime(loginUser.getExpireTime())
                .build();

        return Result.success(loginVO);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/info")
    public Result<UserInfoVO> getUserInfo() {
        // 1. 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.error(ResultCode.UNAUTHORIZED,"用户未登录");
        }
        
        // 2. 获取用户信息
        UserInfoVO userInfo = userService.getUserInfo(loginUser.getUserId());
        
        // 3. 设置角色和权限
        userInfo.setRoles(loginUser.getRoles().stream().toList());
        userInfo.setPermissions(loginUser.getPermissions());
        
        return Result.success(userInfo);
    }

    /**
     * 获取用户菜单路由
     */
    @GetMapping("/user/route")
    public Result<List<MenuVO>> getUserRoutes() {
        // 1. 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.error(ResultCode.UNAUTHORIZED,"用户未登录");
        }
        
        // 2. 获取用户菜单路由
        List<MenuVO> routes = menuService.getUserMenuRoutes(loginUser.getUserId());
        
        return Result.success(routes);
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        loginService.logout();
        return Result.success();
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public Result<LoginVO> refreshToken(@RequestParam String token) {
        String newToken = loginService.refreshToken(token);

        LoginVO loginVO = LoginVO.builder()
                .token(newToken)
                .build();

        return Result.success(loginVO);
    }

    /**
     * 验证用户是否具有指定权限
     */
    @GetMapping("/auth/verifyPermission")
    public Result<Boolean> verifyPermission(@RequestParam String permission) {
        // 1. 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.error(ResultCode.UNAUTHORIZED,"用户未登录");
        }

        // 2. 验证权限
        boolean hasPermission = permissionService.hasPermission(permission);

        return Result.success(hasPermission);
    }

    /**
     * 验证用户是否具有指定角色
     */
    @GetMapping("/auth/verifyRole")
    public Result<Boolean> verifyRole(@RequestParam String role) {
        // 1. 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.error(ResultCode.UNAUTHORIZED,"用户未登录");
        }

        // 2. 验证角色
        boolean hasRole = permissionService.hasRole(role);

        return Result.success(hasRole);
    }
}