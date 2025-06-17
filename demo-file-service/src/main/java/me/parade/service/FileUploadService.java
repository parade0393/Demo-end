package me.parade.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.parade.config.FileUploadProperties;
import me.parade.dto.FileUploadResponse;
import me.parade.entity.FileInfo;
import me.parade.entity.FileReference;
import me.parade.entity.UploadLog;
import me.parade.exception.BusinessException;
import me.parade.exception.FileSizeExceededException;
import me.parade.exception.FileStorageException;
import me.parade.exception.UnsupportedFileTypeException;
import me.parade.mapper.FileInfoMapper;
import me.parade.mapper.FileReferenceMapper;
import me.parade.mapper.UploadLogMapper;
import me.parade.util.FileHashUtil;
import me.parade.util.FileNameUtil;
import me.parade.util.FileTypeUtil;
import me.parade.util.ImageCompressUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件上传服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadProperties fileUploadProperties;
    private final FileInfoMapper fileInfoMapper;
    private final FileReferenceMapper fileReferenceMapper;
    private final UploadLogMapper uploadLogMapper;

    /**
     * 上传文件
     *
     * @param file    上传的文件
     * @param request HTTP请求
     * @return 上传响应
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse uploadFile(MultipartFile file, HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 创建上传日志
        UploadLog uploadLog = createUploadLog(file, request, startTime);
        uploadLogMapper.insert(uploadLog);

        try {
            // 发送开始上传进度

            // 1. 文件验证
            validateFile(file);

            // 2. 计算文件哈希值
            String fileHash = FileHashUtil.calculateHash(file, fileUploadProperties.getDeduplication().getAlgorithm());

            // 3. 检查文件去重
            FileInfo existingFile = null;
            boolean isDuplicate = false;
            if (fileUploadProperties.getDeduplication().getEnabled()) {
                existingFile = fileInfoMapper.selectByFileHash(fileHash);
                isDuplicate = existingFile != null;
            }

            FileInfo fileInfo;
            if (isDuplicate) {
                // 文件已存在，创建新引用
                fileInfo = existingFile;
                fileInfoMapper.incrementReferenceCount(fileInfo.getId());
            } else {
                // 新文件，需要存储
                fileInfo = storeNewFile(file, fileHash, request);
            }

            // 4. 创建文件引用
            FileReference fileReference = createFileReference(fileInfo.getId(), file.getOriginalFilename(), request);
            fileReferenceMapper.insert(fileReference);

            // 5. 更新上传日志
            long endTime = System.currentTimeMillis();
            updateUploadLog(uploadLog, fileInfo.getId(), UploadLog.UploadStatus.SUCCESS, null, endTime - startTime);


            // 6. 构建响应
            return buildUploadResponse(fileInfo, fileReference, isDuplicate, endTime - startTime);

        } catch (Exception e) {
            log.error("File upload failed for: {}", file.getOriginalFilename(), e);
            
            // 更新上传日志
            long endTime = System.currentTimeMillis();
            updateUploadLog(uploadLog, null, UploadLog.UploadStatus.FAILED, e.getMessage(), endTime - startTime);
            
            // 发送失败进度

            throw e;
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"文件不能为空");
        }

        // 1. 检查文件大小
        long fileSize = file.getSize();
        long maxSize = fileUploadProperties.getMaxSize();
        if (fileSize > maxSize) {
            throw new FileSizeExceededException(file.getOriginalFilename(), fileSize, maxSize);
        }

        // 2. 检查文件名安全性
        String originalFilename = file.getOriginalFilename();
        if (!FileNameUtil.isSafeFilename(originalFilename)) {
            throw new BusinessException("文件名包含不安全字符: " + originalFilename);
        }

        if (FileNameUtil.containsSuspiciousContent(originalFilename)) {
            throw new BusinessException( "检测到可疑文件: " + originalFilename);
        }

        // 3. 检查文件类型
        String mimeType = FileTypeUtil.detectMimeType(file);
        
        // 检查是否为可执行文件
        if (FileTypeUtil.isExecutable(mimeType)) {
            throw new UnsupportedFileTypeException(mimeType, originalFilename);
        }

        // 检查是否在允许的类型列表中
        if (!fileUploadProperties.getAllowedTypes().contains(mimeType)) {
            throw new UnsupportedFileTypeException(mimeType, originalFilename);
        }

        log.info("File validation passed: {} ({})", originalFilename, mimeType);
    }

    /**
     * 存储新文件
     */
    private FileInfo storeNewFile(MultipartFile file, String fileHash, HttpServletRequest request) {
        try {
            // 1. 生成存储文件名和路径
            String storedName = FileNameUtil.generateSafeStoredName(file.getOriginalFilename());
            String relativePath = FileNameUtil.generateRelativePath(storedName);
            String basePath = fileUploadProperties.getCurrentBasePath();
            Path fullPath = Paths.get(basePath, relativePath);

            // 2. 创建目录
            Files.createDirectories(fullPath.getParent());

            // 3. 检测文件类型
            String mimeType = FileTypeUtil.detectMimeType(file);
            boolean isImage = FileTypeUtil.isImage(mimeType);

            // 4. 处理文件内容
            byte[] fileContent;
            boolean compressed = false;

            if (isImage && fileUploadProperties.getImage().getCompress().getEnabled()) {
                // 图片压缩
                try {
                    fileContent = ImageCompressUtil.compressImage(
                            file,
                            fileUploadProperties.getImage().getCompress().getQuality(),
                            fileUploadProperties.getImage().getCompress().getMaxWidth(),
                            fileUploadProperties.getImage().getCompress().getMaxHeight()
                    );
                    compressed = true;
                    log.info("Image compressed: {} -> {} bytes", file.getSize(), fileContent.length);
                } catch (Exception e) {
                    log.warn("Image compression failed, using original file: {}", e.getMessage());
                    fileContent = file.getBytes();
                }
            } else {
                // 普通文件直接存储
                fileContent = file.getBytes();
            }

            // 5. 写入文件
            Files.write(fullPath, fileContent);

            // 6. 创建文件信息记录
            FileInfo fileInfo = new FileInfo()
                    .setOriginalName(file.getOriginalFilename())
                    .setStoredName(storedName)
                    .setFilePath(relativePath)
                    .setFileSize(compressed ? (long) fileContent.length : file.getSize())
                    .setMimeType(mimeType)
                    .setFileHash(fileHash)
                    .setIsImage(isImage)
                    .setCompressed(compressed)
                    .setReferenceCount(1)
                    .setUploadTime(LocalDateTime.now())
                    .setUploaderIp(getClientIpAddress(request))
                    .setUserAgent(request.getHeader("User-Agent"));

            fileInfoMapper.insert(fileInfo);

            log.info("File stored successfully: {} -> {}", file.getOriginalFilename(), relativePath);
            return fileInfo;

        } catch (IOException e) {
            throw new FileStorageException(file.getOriginalFilename(), "文件存储失败", e);
        }
    }

    /**
     * 创建文件引用
     */
    private FileReference createFileReference(Long fileId, String originalFilename, HttpServletRequest request) {
        return new FileReference()
                .setFileId(fileId)
                .setReferenceName(originalFilename)
                .setReferenceType("upload")
                .setUploaderInfo(getClientIpAddress(request))
                .setUploadTime(LocalDateTime.now());
    }

    /**
     * 创建上传日志
     */
    private UploadLog createUploadLog(MultipartFile file, HttpServletRequest request, long startTime) {
        return new UploadLog()
                .setOriginalName(file.getOriginalFilename())
                .setFileSize(file.getSize())
                .setMimeType(file.getContentType())
                .setUploadStatus(UploadLog.UploadStatus.PROCESSING.getCode())
                .setUploaderIp(getClientIpAddress(request))
                .setUserAgent(request.getHeader("User-Agent"))
                .setUploadStartTime(LocalDateTime.now());
    }

    /**
     * 更新上传日志
     */
    private void updateUploadLog(UploadLog uploadLog, Long fileId, UploadLog.UploadStatus status, 
                                String errorMessage, long processingTime) {
        uploadLog.setFileId(fileId)
                .setUploadStatus(status.getCode())
                .setErrorMessage(errorMessage)
                .setUploadEndTime(LocalDateTime.now())
                .setProcessingTimeMs(processingTime);
        
        uploadLogMapper.updateById(uploadLog);
    }

    /**
     * 构建上传响应
     */
    private FileUploadResponse buildUploadResponse(FileInfo fileInfo, FileReference fileReference, 
                                                  boolean isDuplicate, long processingTime) {
        FileUploadResponse response = new FileUploadResponse();
        response.setFileId(fileInfo.getId());
        response.setReferenceId(fileReference.getId());
        response.setOriginalName(fileInfo.getOriginalName());
        response.setStoredName(fileInfo.getStoredName());
        response.setFileUrl("/files/" + fileInfo.getFilePath());
        response.setFileSize(fileInfo.getFileSize());
        response.setMimeType(fileInfo.getMimeType());
        response.setIsImage(fileInfo.getIsImage());
        response.setCompressed(fileInfo.getCompressed());
        response.setFileHash(fileInfo.getFileHash());
        response.setIsDuplicate(isDuplicate);
        response.setUploadTime(fileInfo.getUploadTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setProcessingTimeMs(processingTime);
        
        return response;
    }





    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

