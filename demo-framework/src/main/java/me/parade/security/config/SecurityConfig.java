package me.parade.security.config;

import lombok.RequiredArgsConstructor;
import me.parade.security.filter.JwtAuthenticationFilter;
import me.parade.security.handler.AccessDeniedHandlerImpl;
import me.parade.security.handler.AuthenticationEntryPointImpl;
import me.parade.security.utils.JwtConfig;
import me.parade.security.utils.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 配置类
 * 负责配置安全过滤链、密码编码器、认证管理器、核心过滤器和白名单配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConfig jwtConfig;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    
    /**
     * 安全过滤链配置
     * 配置安全过滤链、认证方式、会话管理、权限规则等
     *
     * @param http HttpSecurity对象
     * @return SecurityFilterChain 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 禁用CSRF，因为使用JWT进行认证，不需要CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 启用CORS跨域支持
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 禁用Session，使用JWT进行无状态认证
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置异常处理器
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 放行白名单路径
                        .requestMatchers(getPermitAllUrls()).permitAll()
                        // 放行OPTIONS请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 放行Swagger相关路径
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 放行健康检查
                        .requestMatchers("/actuator/**").permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated())
                // 添加JWT过滤器，在用户名密码认证过滤器之前
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 认证管理器
     * 用于处理认证请求
     *
     * @param authenticationConfiguration 认证配置
     * @return AuthenticationManager 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * JWT认证过滤器
     * 用于处理JWT令牌的提取和验证
     *
     * @return JwtAuthenticationFilter JWT认证过滤器
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtConfig, jwtTokenUtil,userDetailsService);
    }

    /**
     * 认证入口点
     * 处理认证异常，如未登录、token无效等
     *
     * @return AuthenticationEntryPointImpl 认证入口点实现
     */
    @Bean
    public AuthenticationEntryPointImpl authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 访问拒绝处理器
     * 处理授权异常，如权限不足等
     *
     * @return AccessDeniedHandlerImpl 访问拒绝处理器实现
     */
    @Bean
    public AccessDeniedHandlerImpl accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    /**
     * CORS配置源
     * 配置跨域资源共享策略
     *
     * @return CorsConfigurationSource CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // 在生产环境中应该限制为特定域名
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList(jwtConfig.getHeaderString()));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 获取白名单URL数组
     * 这些URL不需要认证即可访问
     *
     * @return String[] 白名单URL数组
     * 这里的路径不能带有context-path的配置
     */
    private String[] getPermitAllUrls() {
        return new String[]{
                // 登录相关
                "/auth/login",
                "/auth/refresh",
                // 验证码
                "/captcha/**",
                // 公开资源
                "/public/**",
                // 静态资源
                "/static/**",
                // 错误页面
                "/error"
        };
    }
}