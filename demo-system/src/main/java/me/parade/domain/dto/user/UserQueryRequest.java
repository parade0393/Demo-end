package me.parade.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询请求参数
 */
@Data
@Schema(description = "用户查询请求参数")
public class UserQueryRequest {
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    
    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String name;
    
    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;
    
    /**
     * 状态（1-正常，0-禁用）
     */
    @Schema(description = "状态（1-正常，0-禁用）")
    private Byte status;
    
    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;
}