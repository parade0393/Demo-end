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
 * 专门用于处理带有@OperationLog注解的方法，记录详细的操作日志
 * 可以将日志记录到数据库中，便于后续查询和分析
 */
@Aspect
@Component
@Order(2) // 优先级低于LogAspect，确保LogAspect先执行
@Slf4j
public class OperationLogAspect {

    /**
     * 配置切入点 - 所有带有@OperationLog注解的方法
     */
    @Pointcut("@annotation(me.parade.aspectj.OperationLog)")
    public void operationLogPointCut() {
    }

    /**
     * 环绕通知，记录操作日志
     *
     * @param joinPoint 切点
     * @return 原方法返回值
     * @throws Throwable 执行原方法时可能抛出的异常
     */
    @Around("operationLogPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求相关信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取OperationLog注解
        OperationLog operationLog = method.getAnnotation(OperationLog.class);
        String module = operationLog.module();
        String operation = operationLog.operation();
        boolean recordParams = operationLog.recordParams();
        boolean recordResult = operationLog.recordResult();
        boolean recordException = operationLog.recordException();
        
        // 构建操作日志对象（实际项目中可能需要保存到数据库）
        // OperationLogEntity logEntity = new OperationLogEntity();
        // 设置基本信息
        String requestUrl = request != null ? request.getRequestURI() : "";
        String requestMethod = request != null ? request.getMethod() : "";
        String requestIp = request != null ? getIpAddress(request) : "";
        String methodName = signature.getDeclaringTypeName() + "." + method.getName();
        String requestParams = recordParams ? getRequestParams(joinPoint) : "";
        
        // 记录请求开始日志
        log.info("========== 操作日志开始 ==========");
        log.info("操作模块: {}", module);
        log.info("操作类型: {}", operation);
        log.info("请求URL: {} {}", requestMethod, requestUrl);
        log.info("请求方法: {}", methodName);
        log.info("请求IP: {}", requestIp);
        if (recordParams) {
            log.info("请求参数: {}", requestParams);
        }
        
        // 执行原方法
        Object result;
        try {
            result = joinPoint.proceed();
            // 记录正常响应日志
            long executionTime = System.currentTimeMillis() - startTime;
            if (recordResult) {
                log.info("响应结果: {}", result);
            }
            log.info("执行时间: {}ms", executionTime);
            log.info("操作状态: 成功");
            
            // 设置操作日志对象的其他信息
            // logEntity.setStatus(1); // 成功
            // logEntity.setOperationTime(executionTime);
            // logEntity.setResponseResult(recordResult ? JSON.toJSONString(result) : "");
        } catch (Throwable e) {
            // 记录异常日志
            long executionTime = System.currentTimeMillis() - startTime;
            if (recordException) {
                log.error("操作异常: {}", e.getMessage(), e);
            }
            log.error("执行时间: {}ms", executionTime);
            log.error("操作状态: 失败");
            
            // 设置操作日志对象的异常信息
            // logEntity.setStatus(0); // 失败
            // logEntity.setOperationTime(executionTime);
            // logEntity.setErrorMsg(e.getMessage());
            
            throw e;
        } finally {
            // 保存操作日志到数据库（实际项目中实现）
            // operationLogService.save(logEntity);
            log.info("========== 操作日志结束 ==========\n");
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