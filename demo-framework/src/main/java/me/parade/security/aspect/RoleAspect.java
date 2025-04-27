package me.parade.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.parade.exception.BusinessException;
import me.parade.security.annotation.RequiresRoles;
import me.parade.security.service.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 角色检查切面
 * <p>
 * 用于拦截标注了{@link RequiresRoles}注解的方法，进行角色检查
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleAspect {

    private final PermissionService permissionService;

    /**
     * 前置通知，在方法执行前进行角色检查
     *
     * @param joinPoint 切点
     */
    @Before("@annotation(me.parade.security.annotation.RequiresRoles)")
    public void checkRole(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取角色注解
        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
        if (requiresRoles == null) {
            return;
        }
        
        // 获取角色标识和验证模式
        String roles = requiresRoles.value();
        RequiresRoles.Logical logical = requiresRoles.logical();
        
        // 进行角色检查
        boolean hasRole;
        if (logical == RequiresRoles.Logical.AND) {
            // 必须具有所有指定角色
            String[] roleArray = roles.split(",");
            hasRole = true;
            for (String role : roleArray) {
                if (!permissionService.hasRole(role.trim())) {
                    hasRole = false;
                    break;
                }
            }
        } else {
            // 只需具有其中一个角色
            hasRole = permissionService.hasAnyRole(roles.split(","));
        }
        
        // 如果没有角色，则抛出异常
        if (!hasRole) {
            log.warn("用户没有角色访问：{}，需要角色：{}", method.getName(), roles);
            throw new BusinessException("没有访问权限");
        }
    }
}