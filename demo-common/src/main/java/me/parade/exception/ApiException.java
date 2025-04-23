package me.parade.exception;

import lombok.Getter;

/**
 * API异常类
 * <p>
 * 用于处理API调用过程中的异常，如参数验证、权限检查等
 * </p>
 */
@Getter
public class ApiException extends RuntimeException {
    
    /**
     * HTTP状态码
     * status的意义在于控制HTTP协议级别的状态码，而不是JSON中的业务码。这对于前端处理HTTP错误非常重要，因为很多HTTP客户端会根据HTTP状态码来触发不同的错误处理流程。
     */
    private int status;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ApiException(String message) {
        this(400, 400, message);
    }
    
    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public ApiException(Integer code, String message) {
        this(400, code, message);
    }
    
    /**
     * 构造函数
     * 
     * @param status HTTP状态码
     * @param code 错误码
     * @param message 错误消息
     */
    public ApiException(int status, Integer code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }
    
    /**
     * 构造函数
     * 
     * @param status HTTP状态码
     * @param code 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public ApiException(int status, Integer code, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code;
        this.message = message;
    }
}