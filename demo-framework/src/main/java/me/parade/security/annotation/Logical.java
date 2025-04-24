package me.parade.security.annotation;

/**
 * 权限验证逻辑类型
 */
public enum Logical {
    /**
     * 必须具有所有的元素
     */
    AND,

    /**
     * 只需具有其中一个元素
     */
    OR
}