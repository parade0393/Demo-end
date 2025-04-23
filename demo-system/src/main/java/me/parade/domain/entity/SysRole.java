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
 * 角色表
 * </p>
 *
 * @author parade
 * @since 2025-04-23 16:37:44
 */
@Getter
@Setter
@ToString
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色编码
     */
    @TableField("code")
    private String code;

    /**
     * 状态（1-正常，0-停用）
     */
    @TableField("status")
    private Byte status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 显示顺序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private Byte isDeleted;
}
