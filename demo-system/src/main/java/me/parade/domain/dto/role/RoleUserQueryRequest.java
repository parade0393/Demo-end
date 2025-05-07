package me.parade.domain.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
/**
 * 角色用户查询请求参数
 */
@Data
@Schema(description = "角色用户查询请求参数")
public class RoleUserQueryRequest {
    
    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", required = true)
    private Long roleId;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
}