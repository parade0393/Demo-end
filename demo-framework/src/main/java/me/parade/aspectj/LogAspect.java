package me.parade.aspectj;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志记录处理切面
 * 
 * 用于记录每个接口的请求参数、响应结果、执行时间等信息
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class LogAspect {

    /**
     * 配置切入点 - 所有controller包下的所有方法
     */
    @Pointcut("execution(* me.parade.controller..*.*(..))")
    public void logPointCut() {
    }

    /**
     * 环绕通知，记录请求的各项信息
     *
     * @param joinPoint 切点
     * @return 原方法返回值
     * @throws Throwable 执行原方法时可能抛出的异常
     */
    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("========== 日志切面开始 ==========");
        long startTime = System.currentTimeMillis();
        
        // 获取请求相关信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 记录请求开始日志
        if (request != null) {
            log.info("========== 请求开始 ==========");
            log.info("请求URL: {} {}", request.getMethod(), request.getRequestURI());
            log.info("请求方法: {}.{}", signature.getDeclaringTypeName(), method.getName());
            log.info("请求IP: {}", getIpAddress(request));
            log.info("请求参数: {}", getRequestParams(joinPoint));
        }
        
        // 执行原方法
        Object result;
        try {
            result = joinPoint.proceed();
            // 记录正常响应日志
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("响应结果: {}", result);
            log.info("执行时间: {}ms", executionTime);
            log.info("========== 请求结束 ==========\n");
        } catch (Throwable e) {
            // 记录异常日志
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("请求异常: {}", e.getMessage());
            log.error("执行时间: {}ms", executionTime);
            log.error("========== 请求异常结束 ==========\n");
            throw e;
        }
        
        return result;
    }
    
    /**
     * 获取请求参数字符串
     *
     * @param joinPoint 切点
     * @return 请求参数字符串
     */
    private String getRequestParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return "无参数";
        }
        
        // 过滤掉HttpServletRequest、HttpServletResponse、MultipartFile等特殊参数
        List<Object> filteredArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest || 
                                arg instanceof HttpServletResponse ||
                                arg instanceof MultipartFile))
                .collect(Collectors.toList());
        
        return filteredArgs.toString();
    }
    
    /**
     * 获取请求IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}