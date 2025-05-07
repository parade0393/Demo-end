package me.parade.domain.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色查询请求参数
 */
@Data
@Schema(description = "角色查询请求参数")
public class RoleQueryRequest {
    
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;
    
    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String code;
    
    /**
     * 状态（1-正常，0-停用）
     */
    @Schema(description = "状态（1-正常，0-停用）")
    private Byte status;
}