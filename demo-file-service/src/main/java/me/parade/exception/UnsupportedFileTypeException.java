package me.parade.exception;

import org.springframework.http.HttpStatus;

/**
 * 文件类型不支持异常
 */
public class UnsupportedFileTypeException extends BusinessException{

    public UnsupportedFileTypeException(String mimeType) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), "不支持的文件类型: " + mimeType);
    }

    public UnsupportedFileTypeException(String mimeType, String filename) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                String.format("不支持的文件类型: %s (文件: %s)", mimeType, filename));
    }
}
