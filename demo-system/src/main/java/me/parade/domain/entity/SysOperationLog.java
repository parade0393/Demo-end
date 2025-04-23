package me.parade.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.parade.domain.common.BaseEntity;

import java.io.Serializable;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author parade
 * @since 2025-04-23 16:37:44
 */
@Getter
@Setter
@ToString
@TableName("sys_operation_log")
public class SysOperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 操作模块
     */
    @TableField("module")
    private String module;

    /**
     * 操作类型
     */
    @TableField("operation")
    private String operation;

    /**
     * 方法名称
     */
    @TableField("method")
    private String method;

    /**
     * 请求URL
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求方式
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求参数
     */
    @TableField("request_param")
    private String requestParam;

    /**
     * 返回结果
     */
    @TableField("response_result")
    private String responseResult;

    /**
     * 错误消息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 操作耗时（毫秒）
     */
    @TableField("operation_time")
    private Integer operationTime;

    /**
     * 操作人ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 操作人用户名
     */
    @TableField("username")
    private String username;

    /**
     * IP地址
     */
    @TableField("ip")
    private String ip;

    /**
     * 操作状态（1-成功，0-失败）
     */
    @TableField("status")
    private Byte status;
}
