package me.parade.security.annotation;

import java.lang.annotation.*;

/**
 * 权限认证注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    /**
     * 需要校验的权限码
     */
    String[] value() default {};

    /**
     * 验证模式：AND | OR
     */
    Logical logical() default Logical.AND;
}