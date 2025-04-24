package me.parade.result;

/**
 * 结果状态码
 */
public interface ResultCode {
    /**
     * 操作成功
     */
    Integer SUCCESS = 200;
    
    /**
     * 操作失败
     */
    Integer ERROR = 500;
    
    /**
     * 参数校验失败
     */
    Integer VALIDATE_FAILED = 400;
    
    /**
     * 未认证
     */
    Integer UNAUTHORIZED = 401;
    
    /**
     * 未授权
     */
    Integer FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    Integer NOT_FOUND = 404;

    Integer BUSINESS_EXCEPTION = -1;
}