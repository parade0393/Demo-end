package me.parade.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型检测工具类
 */
@Slf4j
public class FileTypeUtil {

    private static final Tika tika = new Tika();

    /**
     * 文件头魔数映射
     */
    private static final Map<String, String> FILE_MAGIC_MAP = new HashMap<>();

    static {
        // 图片类型
        FILE_MAGIC_MAP.put("FFD8FF", "image/jpeg");
        FILE_MAGIC_MAP.put("89504E47", "image/png");
        FILE_MAGIC_MAP.put("47494638", "image/gif");
        FILE_MAGIC_MAP.put("424D", "image/bmp");
        FILE_MAGIC_MAP.put("52494646", "image/webp");
        
        // 文档类型
        FILE_MAGIC_MAP.put("25504446", "application/pdf");
        FILE_MAGIC_MAP.put("D0CF11E0", "application/msword");
        FILE_MAGIC_MAP.put("504B0304", "application/zip");
        
        // 压缩文件
        FILE_MAGIC_MAP.put("526172211A07", "application/x-rar-compressed");
        FILE_MAGIC_MAP.put("1F8B08", "application/gzip");
        
        // 可执行文件 (需要拒绝的)
        FILE_MAGIC_MAP.put("4D5A", "application/x-msdownload"); // .exe
        FILE_MAGIC_MAP.put("7F454C46", "application/x-executable"); // ELF
        FILE_MAGIC_MAP.put("CAFEBABE", "application/java-vm"); // .class
    }

    /**
     * 检测文件的真实MIME类型
     *
     * @param file 上传的文件
     * @return 真实的MIME类型
     */
    public static String detectMimeType(MultipartFile file) {
        try {
            // 使用Apache Tika检测MIME类型
            String tikaType = tika.detect(file.getInputStream(), file.getOriginalFilename());
            
            // 使用文件头魔数验证
            String magicType = detectByMagicNumber(file.getInputStream());
            
            // 如果两种方法检测结果一致，返回结果
            if (tikaType.equals(magicType)) {
                return tikaType;
            }
            
            // 如果不一致，优先使用魔数检测结果（更可靠）
            if (magicType != null) {
                log.warn("MIME type mismatch for file {}: Tika={}, Magic={}", 
                        file.getOriginalFilename(), tikaType, magicType);
                return magicType;
            }
            
            return tikaType;
        } catch (IOException e) {
            log.error("Failed to detect MIME type for file: {}", file.getOriginalFilename(), e);
            return "application/octet-stream";
        }
    }

    /**
     * 通过文件头魔数检测文件类型
     *
     * @param inputStream 文件输入流
     * @return MIME类型
     */
    private static String detectByMagicNumber(InputStream inputStream) {
        try {
            // 读取文件头前8个字节
            byte[] header = new byte[8];
            inputStream.mark(8);
            int bytesRead = inputStream.read(header);
            inputStream.reset();
            
            if (bytesRead < 2) {
                return null;
            }
            
            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < bytesRead; i++) {
                String hex = Integer.toHexString(header[i] & 0xFF).toUpperCase();
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
            
            String headerHex = hexString.toString();
            
            // 检查是否匹配已知的文件头
            for (Map.Entry<String, String> entry : FILE_MAGIC_MAP.entrySet()) {
                if (headerHex.startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
            
            return null;
        } catch (IOException e) {
            log.error("Failed to read file header", e);
            return null;
        }
    }

    /**
     * 检查文件是否为图片类型
     *
     * @param mimeType MIME类型
     * @return 是否为图片
     */
    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * 检查文件是否为可执行文件（需要拒绝）
     *
     * @param mimeType MIME类型
     * @return 是否为可执行文件
     */
    public static boolean isExecutable(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        
        return mimeType.equals("application/x-msdownload") ||
               mimeType.equals("application/x-executable") ||
               mimeType.equals("application/java-vm") ||
               mimeType.equals("application/x-dosexec") ||
               mimeType.equals("application/x-mach-binary");
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名（不包含点）
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 根据MIME类型推荐文件扩展名
     *
     * @param mimeType MIME类型
     * @return 推荐的文件扩展名
     */
    public static String getRecommendedExtension(String mimeType) {
        if (mimeType == null) {
            return "bin";
        }
        
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            case "image/bmp":
                return "bmp";
            case "application/pdf":
                return "pdf";
            case "text/plain":
                return "txt";
            case "application/msword":
                return "doc";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return "docx";
            case "application/vnd.ms-excel":
                return "xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return "xlsx";
            case "application/zip":
                return "zip";
            case "application/x-rar-compressed":
                return "rar";
            default:
                return "bin";
        }
    }
}

