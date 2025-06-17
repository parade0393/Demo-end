package me.parade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文件引用实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file_reference")
public class FileReference {
    /**
     * 引用ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 引用名称(用户看到的文件名)
     */
    @TableField("reference_name")
    private String referenceName;

    /**
     * 引用类型(upload:上传, copy:复制等)
     */
    @TableField("reference_type")
    private String referenceType;

    /**
     * 上传者信息
     */
    @TableField("uploader_info")
    private String uploaderInfo;

    /**
     * 引用创建时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 逻辑删除(0:未删除, 1:已删除)
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
