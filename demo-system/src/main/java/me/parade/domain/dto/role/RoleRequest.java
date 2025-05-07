package me.parade.domain.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



/**
 * 角色请求参数
 */
@Data
@Schema(description = "角色请求参数")
public class RoleRequest {
    
    /**
     * 角色ID（更新时必填）
     */
    @Schema(description = "角色ID（更新时必填）")
    private Long id;
    
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", required = true)
    private String name;
    
    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码", required = true)
    private String code;
    
    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序", required = true)
    private Integer sort;
    
    /**
     * 状态（1-正常，0-停用）
     */
    @Schema(description = "状态（1-正常，0-停用）")
    private Byte status;
    
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}