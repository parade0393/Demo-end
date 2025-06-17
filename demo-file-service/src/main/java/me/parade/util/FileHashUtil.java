package me.parade.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件哈希工具类
 */
@Slf4j
public class FileHashUtil {

    /**
     * 计算文件的MD5值
     *
     * @param file 文件
     * @return MD5值
     */
    public static String calculateMD5(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException e) {
            log.error("Failed to calculate MD5 for file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("计算文件MD5失败", e);
        }
    }

    /**
     * 计算文件的SHA256值
     *
     * @param file 文件
     * @return SHA256值
     */
    public static String calculateSHA256(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return DigestUtils.sha256Hex(inputStream);
        } catch (IOException e) {
            log.error("Failed to calculate SHA256 for file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("计算文件SHA256失败", e);
        }
    }

    /**
     * 根据算法类型计算文件哈希值
     *
     * @param file      文件
     * @param algorithm 算法类型 (MD5/SHA256)
     * @return 哈希值
     */
    public static String calculateHash(MultipartFile file, String algorithm) {
        if ("SHA256".equalsIgnoreCase(algorithm)) {
            return calculateSHA256(file);
        } else {
            return calculateMD5(file);
        }
    }

    /**
     * 计算输入流的MD5值
     *
     * @param inputStream 输入流
     * @return MD5值
     */
    public static String calculateMD5(InputStream inputStream) {
        try {
            return DigestUtils.md5Hex(inputStream);
        } catch (IOException e) {
            log.error("Failed to calculate MD5 for input stream", e);
            throw new RuntimeException("计算输入流MD5失败", e);
        }
    }

    /**
     * 计算输入流的SHA256值
     *
     * @param inputStream 输入流
     * @return SHA256值
     */
    public static String calculateSHA256(InputStream inputStream) {
        try {
            return DigestUtils.sha256Hex(inputStream);
        } catch (IOException e) {
            log.error("Failed to calculate SHA256 for input stream", e);
            throw new RuntimeException("计算输入流SHA256失败", e);
        }
    }

    /**
     * 计算字节数组的MD5值
     *
     * @param data 字节数组
     * @return MD5值
     */
    public static String calculateMD5(byte[] data) {
        return DigestUtils.md5Hex(data);
    }

    /**
     * 计算字节数组的SHA256值
     *
     * @param data 字节数组
     * @return SHA256值
     */
    public static String calculateSHA256(byte[] data) {
        return DigestUtils.sha256Hex(data);
    }

    /**
     * 验证文件哈希值
     *
     * @param file         文件
     * @param expectedHash 期望的哈希值
     * @param algorithm    算法类型
     * @return 是否匹配
     */
    public static boolean verifyHash(MultipartFile file, String expectedHash, String algorithm) {
        String actualHash = calculateHash(file, algorithm);
        return expectedHash.equalsIgnoreCase(actualHash);
    }

    /**
     * 生成文件唯一标识（基于文件内容和大小）
     *
     * @param file 文件
     * @return 唯一标识
     */
    public static String generateFileIdentifier(MultipartFile file) {
        try {
            String contentHash = calculateMD5(file);
            long fileSize = file.getSize();
            String originalName = file.getOriginalFilename();
            
            // 组合内容哈希、文件大小和原始文件名生成唯一标识
            String combined = contentHash + "_" + fileSize + "_" + (originalName != null ? originalName : "");
            return DigestUtils.sha256Hex(combined);
        } catch (Exception e) {
            log.error("Failed to generate file identifier", e);
            throw new RuntimeException("生成文件唯一标识失败", e);
        }
    }

    /**
     * 检查是否支持指定的哈希算法
     *
     * @param algorithm 算法名称
     * @return 是否支持
     */
    public static boolean isSupportedAlgorithm(String algorithm) {
        try {
            MessageDigest.getInstance(algorithm);
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
}

