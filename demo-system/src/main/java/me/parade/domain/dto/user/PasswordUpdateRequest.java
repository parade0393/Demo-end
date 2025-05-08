package me.parade.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 密码修改请求参数
 */
@Data
@Schema(description = "密码修改请求参数")
public class PasswordUpdateRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", required = true)
    private String newPassword;
}