package me.parade.aspectj;

import me.parade.annotation.ResponseResult;
import me.parade.result.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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
        System.out.println(body);
        // 如果返回值为null，则返回成功响应
        if (body == null) {
            return Result.success();
        }
        
        // 如果返回值已经是Result类型，则直接返回
        if (body instanceof Result) {
            return body;
        }
        System.out.println("1212121212");
        // 将返回值包装成统一的Result格式
        return Result.success(body);
    }
}