package me.parade.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.parade.annotation.ResponseResult;
import me.parade.dto.FileUploadResponse;
import me.parade.result.Result;
import me.parade.service.FileUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@ResponseResult
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 单文件上传
     *
     * @param file    上传的文件
     * @param request HTTP请求
     * @return 上传结果
     */
    @PostMapping("/single")
    public Result<FileUploadResponse> uploadSingleFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        log.info("Received file upload request: {}", file.getOriginalFilename());

        try {
            FileUploadResponse response = fileUploadService.uploadFile(file, request);

            return Result.success(response);

        } catch (Exception e) {
            log.error("File upload failed", e);

            return Result.error(e.getMessage());
        }
    }

    /**
     * 多文件上传
     *
     * @param files   上传的文件数组
     * @param request HTTP请求
     * @return 上传结果
     */
    @PostMapping("/multiple")
    public Result<Map<String, Object>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            HttpServletRequest request) {

        log.info("Received multiple file upload request: {} files", files.length);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> successFiles = new HashMap<>();
        Map<String, Object> failedFiles = new HashMap<>();

        for (MultipartFile file : files) {
            try {
                FileUploadResponse response = fileUploadService.uploadFile(file, request);
                successFiles.put(file.getOriginalFilename(), response);
            } catch (Exception e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
                Map<String, String> error = new HashMap<>();
                error.put("message", e.getMessage());
                error.put("error", e.getClass().getSimpleName());
                failedFiles.put(file.getOriginalFilename(), error);
            }
        }

        result.put("message", String.format("成功上传 %d 个文件，失败 %d 个文件",
                successFiles.size(), failedFiles.size()));
        result.put("successFiles", successFiles);
        result.put("failedFiles", failedFiles);

        return Result.success(result);
    }

    /**
     * 获取上传配置信息
     *
     * @return 配置信息
     */
    @GetMapping("/config")
    //TODO 查询数据库
    public Result<Map<String, Object>> getUploadConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("maxFileSize", "100MB");
        config.put("allowedTypes", new String[]{
                "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp",
                "application/pdf", "text/plain",
                "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/zip", "application/x-rar-compressed"
        });
        config.put("imageCompressionEnabled", true);
        config.put("deduplicationEnabled", true);

        return Result.success(config);
    }

}
