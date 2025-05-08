package me.parade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.parade.annotation.ResponseResult;
import me.parade.domain.dto.user.PasswordUpdateRequest;
import me.parade.domain.dto.user.UserQueryRequest;
import me.parade.domain.dto.user.UserRequest;
import me.parade.domain.entity.SysUser;
import me.parade.domain.vo.UserVO;
import me.parade.security.annotation.RequiresPermission;
import me.parade.service.DeptService;
import me.parade.service.RoleService;
import me.parade.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户接口", description = "提供用户管理相关接口")
@RequiredArgsConstructor
@ResponseResult
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final DeptService deptService;

    /**
     * 用户列表查询
     *
     * @param current 当前页
     * @param size 每页大小
     * @param queryRequest 查询条件
     * @return 用户分页列表
     */
    @Operation(summary = "用户列表查询", description = "分页查询用户列表，可根据用户名、真实姓名、手机号、状态和部门ID过滤，当传入部门ID时查询该部门及其所有子部门的用户")
    @GetMapping("/list")
    @RequiresPermission("system:user:query")
    public IPage<UserVO> getUserList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "查询条件") UserQueryRequest queryRequest) {
        
        // 构建查询条件
        SysUser user = new SysUser();
        if (queryRequest != null) {
            BeanUtils.copyProperties(queryRequest, user);
        }
        
        // 分页查询
        Page<SysUser> page = new Page<>(current, size);
        IPage<SysUser> userPage;
        
        // 如果传入了部门ID，则查询该部门及其所有子部门的用户
        if (user.getDeptId() != null) {
            // 获取部门及其所有子部门ID
            List<Long> deptIds = deptService.getDeptAndChildrenIds(user.getDeptId());
            userPage = userService.getUserPageByDeptIds(page, user, deptIds);
        } else {
            // 没有传入部门ID，查询所有用户
            userPage = userService.getUserPage(page, user);
        }
        
        // 转换为VO
        IPage<UserVO> voPage = userPage.convert(this::convertToUserVO);
        
        return voPage;
    }

    /**
     * 用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Operation(summary = "用户详情", description = "根据用户ID获取用户详情")
    @GetMapping("/{userId}")
    @RequiresPermission("system:user:query")
    public UserVO getUserDetail(@Parameter(description = "用户ID") @PathVariable Long userId) {
        SysUser user = userService.getById(userId);
        return convertToUserVO(user);
    }

    /**
     * 新增用户
     *
     * @param userRequest 用户信息
     * @return 是否成功
     */
    @Operation(summary = "新增用户", description = "新增用户信息")
    @PostMapping("/create")
    @RequiresPermission("system:user:add")
    public boolean createUser(@Parameter(description = "用户信息") @RequestBody @Valid UserRequest userRequest) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userRequest, user);
        return userService.addUser(user, userRequest.getRoleIds());
    }

    /**
     * 修改用户
     *
     * @param userRequest 用户信息
     * @return 是否成功
     */
    @Operation(summary = "修改用户", description = "修改用户信息")
    @PostMapping("/update")
    @RequiresPermission("system:user:edit")
    public boolean updateUser(@Parameter(description = "用户信息") @RequestBody @Valid UserRequest userRequest) {
        if (userRequest.getId() == null) {
            return false;
        }
        
        // 更新用户基本信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userRequest, user);
        boolean result = userService.updateUser(user);
        
        // 更新用户角色关联
        if (result && userRequest.getRoleIds() != null) {
            userService.assignUserRoles(user.getId(), userRequest.getRoleIds());
        }
        
        return result;
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @GetMapping("/delete")
    @RequiresPermission("system:user:delete")
    public boolean deleteUser(@Parameter(description = "用户ID") @RequestParam Long userId) {
        return userService.deleteUser(userId);
    }

    /**
     * 重置用户密码
     *
     * @param passwordRequest 密码重置请求
     * @return 是否成功
     */
    @Operation(summary = "重置用户密码", description = "重置用户密码")
    @PostMapping("/reset-password")
    @RequiresPermission("system:user:resetPwd")
    public boolean resetPassword(@Parameter(description = "密码重置请求") @RequestBody @Valid PasswordUpdateRequest passwordRequest) {
        return userService.resetPassword(passwordRequest.getUserId(), passwordRequest.getNewPassword());
    }

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态(0:禁用,1:正常)
     * @return 是否成功
     */
    @Operation(summary = "修改用户状态", description = "修改用户状态")
    @GetMapping("/change-status")
    @RequiresPermission("system:user:edit")
    public boolean changeStatus(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "状态(0:禁用,1:正常)") @RequestParam Integer status) {
        return userService.changeStatus(userId, status);
    }

    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    @Operation(summary = "分配用户角色", description = "为用户分配角色")
    @PostMapping("/assign-roles")
    @RequiresPermission("system:user:edit")
    public boolean assignUserRoles(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "角色ID列表") @RequestBody List<Long> roleIds) {
        return userService.assignUserRoles(userId, roleIds);
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Operation(summary = "获取用户角色ID列表", description = "获取用户拥有的角色ID列表")
    @GetMapping("/{userId}/roles")
    @RequiresPermission("system:user:query")
    public List<Long> getUserRoleIds(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return userService.getUserRoleIds(userId);
    }
    
    /**
     * 将用户实体转换为视图对象
     *
     * @param user 用户实体
     * @return 用户视图对象
     */
    private UserVO convertToUserVO(SysUser user) {
        if (user == null) {
            return null;
        }
        
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        
        // 设置状态名称
        vo.setStatusName(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用");
        
        // 设置部门名称
        if (user.getDeptId() != null) {
            vo.setDeptName(deptService.getDeptNameById(user.getDeptId()));
        }
        
        // 设置角色信息
        List<Long> roleIds = roleService.getUserRoleIds(user.getId());
        vo.setRoleIds(roleIds);
        
        // 设置角色名称列表
        if (roleIds != null && !roleIds.isEmpty()) {
            List<String> roleNames = new ArrayList<>();
            for (Long roleId : roleIds) {
                String roleName = roleService.getRoleNameById(roleId);
                if (roleName != null) {
                    roleNames.add(roleName);
                }
            }
            vo.setRoleNames(roleNames);
        }
        
        return vo;
    }
}