package me.parade.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 */
@Data
@Schema(description = "用户视图对象")
public class UserVO {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long id;
    
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
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    
    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;
    
    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String deptName;
    
    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;
    
    /**
     * 状态（1-正常，0-禁用）
     */
    @Schema(description = "状态（1-正常，0-禁用）")
    private Byte status;
    
    /**
     * 状态名称
     */
    @Schema(description = "状态名称")
    private String statusName;
    
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    
    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 角色ID列表
     */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
    
    /**
     * 角色名称列表
     */
    @Schema(description = "角色名称列表")
    private List<String> roleNames;
}