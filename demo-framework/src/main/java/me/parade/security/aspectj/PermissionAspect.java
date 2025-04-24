package me.parade.security.aspectj;

import lombok.RequiredArgsConstructor;
import me.parade.exception.BusinessException;
import me.parade.result.ResultCode;
import me.parade.security.annotation.RequiresPermissions;
import me.parade.security.annotation.RequiresRoles;
import me.parade.service.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 权限验证切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;

    /**
     * 权限验证切入点
     */
    @Pointcut("@annotation(requiresPermissions)")
    public void permissionPointcut(RequiresPermissions requiresPermissions) {
    }

    /**
     * 角色验证切入点
     */
    @Pointcut("@annotation(requiresRoles)")
    public void rolePointcut(RequiresRoles requiresRoles) {
    }

    /**
     * 权限验证
     */
    @Before("permissionPointcut(requiresPermissions)")
    public void doBefore(JoinPoint point, RequiresPermissions requiresPermissions) {
        String[] permissions = requiresPermissions.value();
        if (permissions.length == 0) {
            return;
        }

        if (requiresPermissions.logical() == me.parade.security.annotation.Logical.AND) {
            // 必须全部拥有
            for (String permission : permissions) {
                if (!permissionService.hasPermission(permission)) {
                    throw new BusinessException(ResultCode.UNAUTHORIZED, "权限不足");
                }
            }
        } else {
            // 只需拥有一个
            boolean hasPermission = false;
            for (String permission : permissions) {
                if (permissionService.hasPermission(permission)) {
                    hasPermission = true;
                    break;
                }
            }
            if (!hasPermission) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "权限不足");
            }
        }
    }

    /**
     * 角色验证
     */
    @Before("rolePointcut(requiresRoles)")
    public void doBefore(JoinPoint point, RequiresRoles requiresRoles) {
        String[] roles = requiresRoles.value();
        if (roles.length == 0) {
            return;
        }

        if (requiresRoles.logical() == me.parade.security.annotation.Logical.AND) {
            // 必须全部拥有
            for (String role : roles) {
                if (!permissionService.hasRole(role)) {
                    throw new BusinessException(ResultCode.UNAUTHORIZED, "权限不足");
                }
            }
        } else {
            // 只需拥有一个
            boolean hasRole = false;
            for (String role : roles) {
                if (permissionService.hasRole(role)) {
                    hasRole = true;
                    break;
                }
            }
            if (!hasRole) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "权限不足");
            }
        }
    }
}