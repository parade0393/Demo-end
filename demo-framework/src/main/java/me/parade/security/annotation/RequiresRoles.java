package me.parade.security.annotation;

import java.lang.annotation.*;

/**
 * 角色认证注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRoles {
    /**
     * 需要校验的角色标识
     */
    String[] value() default {};

    /**
     * 验证模式：AND | OR
     */
    Logical logical() default Logical.AND;
}