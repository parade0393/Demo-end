package me.parade.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * 用户创建/更新请求参数
 */
@Data
@Schema(description = "用户创建/更新请求参数")
public class UserRequest {
    
    /**
     * 用户ID（更新时必填）
     */
    @Schema(description = "用户ID（更新时必填）")
    private Long id;
    
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", required = true)
    private String username;
    
    /**
     * 密码（创建时必填）
     */
    @Schema(description = "密码（创建时必填）")
    private String password;
    
    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Schema(description = "真实姓名", required = true)
    private String name;
    
    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;
    
    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱")
    private String email;
    
    /**
     * 部门ID
     */
    @NotNull(message = "部门ID不能为空")
    @Schema(description = "部门ID", required = true)
    private Long deptId;
    
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
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
    
    /**
     * 角色ID列表
     */
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}