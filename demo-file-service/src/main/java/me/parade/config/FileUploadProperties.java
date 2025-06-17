package me.parade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
    /**
     * 存储路径配置
     */
    private Map<String, String> basePath;

    /**
     * 允许的文件类型
     */
    private List<String> allowedTypes;

    /**
     * 文件大小限制 (字节)
     */
    private Long maxSize;

    /**
     * 图片配置
     */
    private ImageConfig image;

    /**
     * 文件去重配置
     */
    private DeduplicationConfig deduplication;

    @Data
    public static class ImageConfig {
        /**
         * 压缩配置
         */
        private CompressConfig compress;

        @Data
        public static class CompressConfig {
            /**
             * 是否启用压缩
             */
            private Boolean enabled;

            /**
             * 压缩质量 (0.0-1.0)
             */
            private Double quality;

            /**
             * 最大宽度
             */
            private Integer maxWidth;

            /**
             * 最大高度
             */
            private Integer maxHeight;
        }
    }

    @Data
    public static class DeduplicationConfig {
        /**
         * 是否启用去重
         */
        private Boolean enabled;

        /**
         * 哈希算法 (MD5/SHA256)
         */
        private String algorithm;
    }

    /**
     * 获取当前操作系统的存储路径
     */
    public String getCurrentBasePath() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return basePath.get("windows");
        } else {
            return basePath.get("linux");
        }
    }
}
