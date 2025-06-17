package me.parade.util;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 文件名安全工具类
 */
@Slf4j
public class FileNameUtil {

    /**
     * 危险字符模式
     */
    private static final Pattern DANGEROUS_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");
    
    /**
     * 路径遍历模式
     */
    private static final Pattern PATH_TRAVERSAL = Pattern.compile("\\.\\.[\\\\/]");
    
    /**
     * 控制字符模式
     */
    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\x00-\\x1f\\x7f]");

    /**
     * 生成安全的存储文件名（使用UUID）
     *
     * @param originalFilename 原始文件名
     * @return 安全的存储文件名
     */
    public static String generateSafeStoredName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        if (extension.isEmpty()) {
            return uuid;
        } else {
            return uuid + "." + extension;
        }
    }

    /**
     * 清理文件名，移除危险字符
     *
     * @param filename 原始文件名
     * @return 清理后的文件名
     */
    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "unnamed_file";
        }

        // 移除路径分隔符和危险字符
        String cleaned = filename.trim();
        
        // 检查路径遍历攻击
        if (PATH_TRAVERSAL.matcher(cleaned).find()) {
            log.warn("Potential path traversal attack detected in filename: {}", filename);
            cleaned = cleaned.replaceAll("\\.\\.", "");
        }
        
        // 移除危险字符
        cleaned = DANGEROUS_CHARS.matcher(cleaned).replaceAll("_");
        
        // 移除控制字符
        cleaned = CONTROL_CHARS.matcher(cleaned).replaceAll("");
        
        // 移除前后空格和点
        cleaned = cleaned.replaceAll("^[\\s.]+|[\\s.]+$", "");
        
        // 如果清理后为空，使用默认名称
        if (cleaned.isEmpty()) {
            cleaned = "unnamed_file";
        }
        
        // 限制文件名长度
        if (cleaned.length() > 200) {
            String extension = getFileExtension(cleaned);
            String nameWithoutExt = getFileNameWithoutExtension(cleaned);
            nameWithoutExt = nameWithoutExt.substring(0, 200 - extension.length() - 1);
            cleaned = nameWithoutExt + "." + extension;
        }
        
        return cleaned;
    }

    /**
     * 验证文件名是否安全
     *
     * @param filename 文件名
     * @return 是否安全
     */
    public static boolean isSafeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        // 检查路径遍历
        if (PATH_TRAVERSAL.matcher(filename).find()) {
            return false;
        }
        
        // 检查危险字符
        if (DANGEROUS_CHARS.matcher(filename).find()) {
            return false;
        }
        
        // 检查控制字符
        if (CONTROL_CHARS.matcher(filename).find()) {
            return false;
        }
        
        // 检查是否以点开头或结尾
        if (filename.startsWith(".") || filename.endsWith(".")) {
            return false;
        }
        
        // 检查长度
        if (filename.length() > 255) {
            return false;
        }
        
        // 检查保留名称（Windows）
        String nameWithoutExt = getFileNameWithoutExtension(filename).toUpperCase();
        String[] reservedNames = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", 
                                 "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", 
                                 "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        
        for (String reserved : reservedNames) {
            if (reserved.equals(nameWithoutExt)) {
                return false;
            }
        }
        
        return true;
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
     * 获取不包含扩展名的文件名
     *
     * @param filename 文件名
     * @return 不包含扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }
        
        return filename.substring(0, lastDotIndex);
    }

    /**
     * 生成带时间戳的文件名
     *
     * @param originalFilename 原始文件名
     * @return 带时间戳的文件名
     */
    public static String generateTimestampedName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String nameWithoutExt = getFileNameWithoutExtension(sanitizeFilename(originalFilename));
        long timestamp = System.currentTimeMillis();
        
        if (extension.isEmpty()) {
            return nameWithoutExt + "_" + timestamp;
        } else {
            return nameWithoutExt + "_" + timestamp + "." + extension;
        }
    }

    /**
     * 生成唯一文件名（基于原始文件名和UUID）
     *
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueDisplayName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String nameWithoutExt = getFileNameWithoutExtension(sanitizeFilename(originalFilename));
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        
        if (extension.isEmpty()) {
            return nameWithoutExt + "_" + shortUuid;
        } else {
            return nameWithoutExt + "_" + shortUuid + "." + extension;
        }
    }

    /**
     * 检查文件名是否包含可疑内容
     *
     * @param filename 文件名
     * @return 是否包含可疑内容
     */
    public static boolean containsSuspiciousContent(String filename) {
        if (filename == null) {
            return false;
        }
        
        String lowerFilename = filename.toLowerCase();
        
        // 检查可疑扩展名组合
        String[] suspiciousPatterns = {
            ".exe.", ".scr.", ".bat.", ".cmd.", ".com.", ".pif.", ".vbs.", ".js.",
            ".jar.", ".app.", ".deb.", ".rpm.", ".dmg.", ".pkg."
        };
        
        for (String pattern : suspiciousPatterns) {
            if (lowerFilename.contains(pattern)) {
                return true;
            }
        }
        
        // 检查双扩展名
        String[] executableExts = {".exe", ".scr", ".bat", ".cmd", ".com", ".pif"};
        for (String ext : executableExts) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 生成文件的相对路径
     *
     * @param storedName 存储文件名
     * @return 相对路径
     */
    public static String generateRelativePath(String storedName) {
        if (storedName == null || storedName.length() < 2) {
            return "misc/" + storedName;
        }
        
        // 使用文件名前两个字符作为目录结构
        String dir1 = storedName.substring(0, 1);
        String dir2 = storedName.substring(1, 2);
        
        return dir1 + "/" + dir2 + "/" + storedName;
    }

    /**
     * 验证路径是否安全
     *
     * @param path 路径
     * @return 是否安全
     */
    public static boolean isSafePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        
        // 规范化路径
        String normalizedPath = path.replace("\\", "/");
        
        // 检查路径遍历
        if (normalizedPath.contains("../") || normalizedPath.contains("..\\")) {
            return false;
        }
        
        // 检查绝对路径（在相对路径上下文中）
        if (normalizedPath.startsWith("/") && !normalizedPath.startsWith("//")) {
            return false;
        }
        
        return true;
    }
}

