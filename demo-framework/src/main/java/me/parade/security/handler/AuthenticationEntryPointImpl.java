package me.parade.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证入口点实现
 * 处理认证异常，如未登录、token无效等
 * 当用户尝试访问需要认证的资源但未提供有效凭证时，将调用此处理器
 */
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        log.error("认证失败：{}", authException.getMessage());
        
        // 检查是否是令牌过期
        boolean isExpired = request.getAttribute("expired") != null && (boolean) request.getAttribute("expired");
        
        // 设置响应状态码和内容类型
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // 构建响应内容
        Map<String, Object> result = new HashMap<>();
        result.put("code", HttpStatus.UNAUTHORIZED.value());
        result.put("message", isExpired ? "令牌已过期，请重新登录" : "未提供有效的认证凭证");
        result.put("expired", isExpired);
        
        // 写入响应
        try (PrintWriter writer = response.getWriter()) {
            ObjectMapper objectMapper = new ObjectMapper();
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }
}