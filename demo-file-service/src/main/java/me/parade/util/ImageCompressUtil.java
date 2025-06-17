package me.parade.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具类
 */
@Slf4j
public class ImageCompressUtil {

    /**
     * 压缩图片
     *
     * @param file       原始图片文件
     * @param quality    压缩质量 (0.0-1.0)
     * @param maxWidth   最大宽度
     * @param maxHeight  最大高度
     * @return 压缩后的图片字节数组
     */
    public static byte[] compressImage(MultipartFile file, double quality, int maxWidth, int maxHeight) {
        try (InputStream inputStream = file.getInputStream()) {
            return compressImage(inputStream, quality, maxWidth, maxHeight, getImageFormat(file.getOriginalFilename()));
        } catch (IOException e) {
            log.error("Failed to compress image: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("图片压缩失败", e);
        }
    }

    /**
     * 压缩图片
     *
     * @param inputStream 图片输入流
     * @param quality     压缩质量 (0.0-1.0)
     * @param maxWidth    最大宽度
     * @param maxHeight   最大高度
     * @param format      图片格式
     * @return 压缩后的图片字节数组
     */
    public static byte[] compressImage(InputStream inputStream, double quality, int maxWidth, int maxHeight, String format) {
        try {
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                throw new RuntimeException("无法读取图片文件");
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 计算压缩后的尺寸
            int[] newSize = calculateNewSize(originalWidth, originalHeight, maxWidth, maxHeight);
            int newWidth = newSize[0];
            int newHeight = newSize[1];

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 使用Thumbnailator进行压缩
            Thumbnails.of(originalImage)
                    .size(newWidth, newHeight)
                    .outputQuality(quality)
                    .outputFormat(format)
                    .toOutputStream(outputStream);

            byte[] compressedData = outputStream.toByteArray();
            
            log.info("Image compressed: {}x{} -> {}x{}, size: {} -> {} bytes, quality: {}", 
                    originalWidth, originalHeight, newWidth, newHeight, 
                    getOriginalSize(originalImage), compressedData.length, quality);

            return compressedData;
        } catch (IOException e) {
            log.error("Failed to compress image", e);
            throw new RuntimeException("图片压缩失败", e);
        }
    }

    /**
     * 智能压缩图片（根据文件大小自动调整压缩参数）
     *
     * @param file           原始图片文件
     * @param targetSizeKB   目标文件大小（KB）
     * @param maxWidth       最大宽度
     * @param maxHeight      最大高度
     * @return 压缩后的图片字节数组
     */
    public static byte[] smartCompressImage(MultipartFile file, int targetSizeKB, int maxWidth, int maxHeight) {
        try (InputStream inputStream = file.getInputStream()) {
            String format = getImageFormat(file.getOriginalFilename());
            
            // 初始压缩质量
            double quality = 0.9;
            byte[] compressedData = compressImage(inputStream, quality, maxWidth, maxHeight, format);
            
            // 如果压缩后仍然过大，继续降低质量
            int attempts = 0;
            int maxAttempts = 5;
            int targetSizeBytes = targetSizeKB * 1024;
            
            while (compressedData.length > targetSizeBytes && attempts < maxAttempts && quality > 0.1) {
                quality -= 0.15;
                if (quality < 0.1) {
                    quality = 0.1;
                }
                
                // 重新读取输入流
                try (InputStream newInputStream = file.getInputStream()) {
                    compressedData = compressImage(newInputStream, quality, maxWidth, maxHeight, format);
                }
                attempts++;
                
                log.debug("Smart compression attempt {}: quality={}, size={} bytes", 
                        attempts, quality, compressedData.length);
            }
            
            log.info("Smart compression completed: target={}KB, actual={}KB, quality={}, attempts={}", 
                    targetSizeKB, compressedData.length / 1024, quality, attempts);
            
            return compressedData;
        } catch (IOException e) {
            log.error("Failed to smart compress image: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("智能图片压缩失败", e);
        }
    }

    /**
     * 计算新的图片尺寸（保持宽高比）
     *
     * @param originalWidth  原始宽度
     * @param originalHeight 原始高度
     * @param maxWidth       最大宽度
     * @param maxHeight      最大高度
     * @return 新的尺寸 [宽度, 高度]
     */
    private static int[] calculateNewSize(int originalWidth, int originalHeight, int maxWidth, int maxHeight) {
        // 如果原始尺寸已经小于等于最大尺寸，不需要缩放
        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return new int[]{originalWidth, originalHeight};
        }

        // 计算缩放比例
        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        return new int[]{newWidth, newHeight};
    }

    /**
     * 获取图片格式
     *
     * @param filename 文件名
     * @return 图片格式
     */
    private static String getImageFormat(String filename) {
        if (filename == null) {
            return "jpg";
        }
        
        String extension = FileTypeUtil.getFileExtension(filename);
        switch (extension.toLowerCase()) {
            case "png":
                return "png";
            case "gif":
                return "gif";
            case "bmp":
                return "bmp";
            case "webp":
                return "webp";
            case "jpeg":
            case "jpg":
            default:
                return "jpg";
        }
    }

    /**
     * 估算原始图片大小
     *
     * @param image 图片对象
     * @return 估算的文件大小（字节）
     */
    private static long getOriginalSize(BufferedImage image) {
        // 简单估算：宽 * 高 * 3（RGB）
        return (long) image.getWidth() * image.getHeight() * 3;
    }

    /**
     * 检查是否需要压缩
     *
     * @param file      文件
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param maxSizeKB 最大文件大小（KB）
     * @return 是否需要压缩
     */
    public static boolean needsCompression(MultipartFile file, int maxWidth, int maxHeight, int maxSizeKB) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return false;
            }

            // 检查尺寸
            boolean sizeExceeded = image.getWidth() > maxWidth || image.getHeight() > maxHeight;
            
            // 检查文件大小
            boolean fileSizeExceeded = file.getSize() > maxSizeKB * 1024L;

            return sizeExceeded || fileSizeExceeded;
        } catch (IOException e) {
            log.error("Failed to check if compression is needed for file: {}", file.getOriginalFilename(), e);
            return false;
        }
    }

    /**
     * 创建图片缩略图
     *
     * @param file   原始图片文件
     * @param width  缩略图宽度
     * @param height 缩略图高度
     * @return 缩略图字节数组
     */
    public static byte[] createThumbnail(MultipartFile file, int width, int height) {
        try (InputStream inputStream = file.getInputStream()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputQuality(0.8)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to create thumbnail for file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("创建缩略图失败", e);
        }
    }
}

