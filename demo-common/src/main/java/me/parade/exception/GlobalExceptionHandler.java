package me.parade.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import me.parade.result.Result;
import me.parade.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验异常（@RequestBody参数校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = getBindingResultErrorMessage(bindingResult);
        logger.warn("参数校验失败: {}", message);
        Result<Void> response = Result.error(ResultCode.VALIDATE_FAILED, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数校验异常（@RequestParam等参数校验）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException e) {
        String message = getBindingResultErrorMessage(e);
        logger.warn("参数校验失败: {}", message);
        Result<Void> response = Result.error(ResultCode.VALIDATE_FAILED, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数校验异常（@Validated方法参数校验）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        Result<Void> response = Result.error(ResultCode.VALIDATE_FAILED, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }




    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        // 创建我们的统一响应格式
        Result<Void> response = Result.error(e.getCode(), e.getMessage());

        // 将统一响应包装在ResponseEntity中，并设置HTTP状态码
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * 处理API异常
     * 前端收到HTTP状态码为 ApiException.status的
     * 响应体中JSON数据包含业务码（在Result的code字段中）
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Result<Void>> handleApiException(ApiException e) {
        logger.warn("API异常: {}", e.getMessage());
        // 创建我们的统一响应格式
        Result<Void> response = Result.error(e.getCode(), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatus()));
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        logger.error("系统异常", e);
        Result<Void> response = Result.error("系统异常，请联系管理员");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 获取参数校验错误信息
     */
    private String getBindingResultErrorMessage(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }
}