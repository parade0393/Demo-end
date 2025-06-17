package me.parade.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 上传日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("upload_log")
public class UploadLog {
    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文件ID(上传成功时关联)
     */
    @TableField("file_id")
    private Long fileId;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

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
     * 上传状态(SUCCESS:成功, FAILED:失败, PROCESSING:处理中)
     */
    @TableField("upload_status")
    private String uploadStatus;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

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
     * 上传开始时间
     */
    @TableField("upload_start_time")
    private LocalDateTime uploadStartTime;

    /**
     * 上传结束时间
     */
    @TableField("upload_end_time")
    private LocalDateTime uploadEndTime;

    /**
     * 处理耗时(毫秒)
     */
    @TableField("processing_time_ms")
    private Long processingTimeMs;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 上传状态枚举
     */
    public enum UploadStatus {
        PROCESSING("PROCESSING", "处理中"),
        SUCCESS("SUCCESS", "成功"),
        FAILED("FAILED", "失败");

        private final String code;
        private final String desc;

        UploadStatus(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
