package me.parade.dto;

import lombok.Data;

@Data
public class FileUploadResponse {
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 引用ID
     */
    private Long referenceId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储文件名
     */
    private String storedName;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * MIME类型
     */
    private String mimeType;

    /**
     * 是否为图片
     */
    private Boolean isImage;

    /**
     * 是否已压缩
     */
    private Boolean compressed;

    /**
     * 文件哈希值
     */
    private String fileHash;

    /**
     * 是否为重复文件
     */
    private Boolean isDuplicate;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTimeMs;
}
