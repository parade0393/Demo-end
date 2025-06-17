package me.parade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file_info")
public class FileInfo {

    /**
     * 文件ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 存储文件名(UUID)
     */
    @TableField("stored_name")
    private String storedName;

    /**
     * 文件存储路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小(字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 文件哈希值(MD5/SHA256)
     */
    @TableField("file_hash")
    private String fileHash;

    /**
     * 是否为图片(0:否, 1:是)
     */
    @TableField("is_image")
    private Boolean isImage;

    /**
     * 是否已压缩(0:否, 1:是)
     */
    @TableField("compressed")
    private Boolean compressed;

    /**
     * 引用计数(用于去重)
     */
    @TableField("reference_count")
    private Integer referenceCount;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 上传者IP
     */
    @TableField("uploader_ip")
    private String uploaderIp;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

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
