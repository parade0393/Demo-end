package me.parade.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.parade.domain.common.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author parade
 * @since 2025-04-23 16:37:44
 */
@Getter
@Setter
@ToString
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父部门ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 负责人
     */
    @TableField("leader")
    private String leader;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 状态（1-正常，0-停用）
     */
    @TableField("status")
    private Byte status;

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
    
    /**
     * 子部门列表
     */
    @TableField(exist = false)
    private List<SysDept> children;
}
