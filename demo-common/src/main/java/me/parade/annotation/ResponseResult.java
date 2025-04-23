package me.parade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 统一响应注解
 * <p>
 * 标记需要被统一响应拦截器处理的控制器方法
 * 可以应用在类或方法上，应用在类上表示该类的所有方法都需要统一响应处理
 * </p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseResult {
}