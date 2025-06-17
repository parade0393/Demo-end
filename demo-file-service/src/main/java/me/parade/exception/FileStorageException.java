package me.parade.exception;

/**
 * 文件存储异常
 */
public class FileStorageException extends BusinessException{
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException( String message,Throwable cause) {
        super(message,cause);
    }

    public FileStorageException(String filename, String message, Throwable cause) {
        super(String.format("文件 %s 存储失败: %s", filename, message), cause);
    }
}
