package me.parade.service.impl;

import lombok.RequiredArgsConstructor;
import me.parade.domain.dto.auth.LoginResponse;
import me.parade.domain.dto.auth.UserInfoResponse;
import me.parade.domain.entity.SysUser;
import me.parade.exception.BusinessException;
import me.parade.mapper.UserMapper;
import me.parade.security.utils.JwtConfig;
import me.parade.security.utils.JwtTokenUtil;
import me.parade.security.utils.SecurityUtils;
import me.parade.service.AuthService;
import me.parade.service.MenuService;
import me.parade.service.RoleService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final MenuService menuService;

    @Override
    public LoginResponse login(String username, String password) {
       try {
           // 进行身份验证
           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(username, password)
           );

           // 认证成功后，将认证信息存入SecurityContext
           //设置 SecurityContext，是为了让本次请求线程能获取到认证用户信息，便于后续业务处理。
           //典型场景：登录后立即返回用户详情、记录登录日志、触发审计等。
           SecurityContextHolder.getContext().setAuthentication(authentication);

           // 生成JWT令牌
           UserDetails userDetails = (UserDetails) authentication.getPrincipal();
           String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
           String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

           // 构建并返回登录响应
           return LoginResponse.builder()
                   .token(accessToken)
                   .refreshToken(refreshToken)
                   .tokenType(jwtConfig.getTokenPrefix().trim())
                   .expiresIn(jwtConfig.getAccessTokenExpiration())
                   .build();
       }catch (Exception e){
           throw new BusinessException(e.getMessage());
       }
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        // 获取当前认证用户名
        String username = SecurityUtils.getUsername();
        
        // 查询用户信息
        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取用户角色和权限
        List<String> roles = roleService.getUserRoleCodes(user.getId());
        List<String> permissions = menuService.getUserPermissions(user.getId());
        
        // 构建并返回用户信息响应
        return UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .avatar(user.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
    
    @Override
    public void logout() {
        // 清除SecurityContext中的认证信息
        SecurityContextHolder.clearContext();
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌是否有效
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            if (username == null) {
                throw new BusinessException("无效的刷新令牌");
            }
            
            // 检查令牌是否过期
            Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(refreshToken);
            if (expirationDate.before(new Date())) {
                throw new BusinessException("刷新令牌已过期");
            }
            
            // 查询用户信息
            SysUser user = userMapper.selectByUsername(username);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 创建UserDetails对象
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password(user.getPassword())
                    .authorities("ROLE_USER")
                    .build();
            
            // 生成新的访问令牌和刷新令牌
            String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
            
            // 构建并返回登录响应
            return LoginResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType(jwtConfig.getTokenPrefix().trim())
                    .expiresIn(jwtConfig.getAccessTokenExpiration())
                    .build();
        } catch (Exception e) {
            throw new BusinessException("刷新令牌失败: " + e.getMessage());
        }
    }
}