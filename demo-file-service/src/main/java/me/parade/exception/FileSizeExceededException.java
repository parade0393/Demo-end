package me.parade.exception;

import org.springframework.http.HttpStatus;

public class FileSizeExceededException extends BusinessException{
    public FileSizeExceededException(long fileSize, long maxSize) {
        super(HttpStatus.BAD_REQUEST.value(), String.format("文件大小超出限制: %d bytes (最大允许: %d bytes)", fileSize, maxSize));
    }

    public FileSizeExceededException(String filename, long fileSize, long maxSize) {
        super(HttpStatus.BAD_REQUEST.value(),
                String.format("文件 %s 大小超出限制: %d bytes (最大允许: %d bytes)", filename, fileSize, maxSize));
    }
}
