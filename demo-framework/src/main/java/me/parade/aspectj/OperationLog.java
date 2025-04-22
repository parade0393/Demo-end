package me.parade.aspectj;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 
 * 用于标记需要记录操作日志的方法，可以指定操作模块和操作类型
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    String operation() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;
    
    /**
     * 是否记录响应结果
     */
    boolean recordResult() default true;
    
    /**
     * 是否记录异常信息
     */
    boolean recordException() default true;
}