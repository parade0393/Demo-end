package me.parade.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色注解
 * <p>
 * 用于标注需要特定角色才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRoles {

    /**
     * 需要的角色标识，多个角色使用逗号分隔
     */
    String value();

    /**
     * 验证模式：AND | OR，默认AND
     */
    Logical logical() default Logical.AND;

    /**
     * 逻辑枚举
     */
    enum Logical {
        /**
         * 必须具有所有指定角色
         */
        AND,
        
        /**
         * 只需具有其中一个角色
         */
        OR
    }
}