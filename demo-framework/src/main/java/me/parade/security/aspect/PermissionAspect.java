package me.parade.security.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.parade.exception.BusinessException;
import me.parade.security.annotation.RequiresPermission;
import me.parade.security.service.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限检查切面
 * <p>
 * 用于拦截标注了{@link RequiresPermission}注解的方法，进行权限检查
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final PermissionService permissionService;

    /**
     * 前置通知，在方法执行前进行权限检查
     *
     * @param joinPoint 切点
     */
    @Before("@annotation(me.parade.security.annotation.RequiresPermission)")
    public void checkPermission(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取权限注解
        RequiresPermission requiresPermission = method.getAnnotation(RequiresPermission.class);
        if (requiresPermission == null) {
            return;
        }
        
        // 获取权限标识和验证模式
        String permission = requiresPermission.value();
        RequiresPermission.Logical logical = requiresPermission.logical();
        
        // 进行权限检查
        boolean hasPermission;
        if (logical == RequiresPermission.Logical.AND) {
            // 必须具有所有指定权限
            hasPermission = permissionService.hasPermission(permission);
        } else {
            // 只需具有其中一个权限
            hasPermission = permissionService.hasAnyPermission(permission.split(","));
        }
        
        // 如果没有权限，则抛出异常
        if (!hasPermission) {
            log.warn("用户没有权限访问：{}，需要权限：{}", method.getName(), permission);
            throw new BusinessException("没有访问权限");
        }
    }
}