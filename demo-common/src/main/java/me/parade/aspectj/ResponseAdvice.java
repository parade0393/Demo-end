package me.parade.aspectj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.parade.annotation.ResponseResult;
import me.parade.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应拦截器
 * <p>
 * 用于自动将控制器返回值包装成统一的响应格式，避免每个控制器方法都手动包装
 * </p>
 */
@RestControllerAdvice(basePackages = {"me.parade"})
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果返回值已经是Result类型，则不需要处理
        if (returnType.getParameterType().equals(Result.class)) {
            return false;
        }
        
        // 检查类或方法是否标记了@ResponseResult注解
        boolean hasClassAnnotation = returnType.getContainingClass().isAnnotationPresent(ResponseResult.class);
        boolean hasMethodAnnotation = returnType.getMethod() != null && 
                                     returnType.getMethod().isAnnotationPresent(ResponseResult.class);
        
        return hasClassAnnotation || hasMethodAnnotation;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 如果返回值为null，则返回成功响应
        if (body == null) {
            return Result.success();
        }
        
        // 如果返回值已经是Result类型，则直接返回
        if (body instanceof Result) {
            return body;
        }
        
        // 特殊处理String类型返回值
        // 因为StringHttpMessageConverter会直接将返回值转换为字符串，而不是JSON
        if (returnType.getParameterType().equals(String.class)) {
            try {
                // 设置Content-Type为application/json
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                // 将Result对象转换为JSON字符串
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("将对象转换为JSON字符串时发生错误", e);
            }
        }
        
        // 将返回值包装成统一的Result格式
        return Result.success(body);
    }
}