package me.parade.security.service.impl;

import lombok.RequiredArgsConstructor;
import me.parade.exception.BusinessException;
import me.parade.result.ResultCode;
import me.parade.domain.dto.LoginUser;
import me.parade.security.service.LoginService;
import me.parade.security.utils.JwtTokenUtil;
import me.parade.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录服务实现
 */
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginUser login(String username, String password) {
        // 1. 检查登录失败次数
        String loginFailKey = "login_fail:" + username;

        // 2. 用户验证
        Authentication authentication;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            // 记录登录失败次数
            throw new BusinessException(ResultCode.FORBIDDEN, "用户名或密码错误");
        }



        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 3. 生成token
        String s = jwtTokenUtil.generateAccessToken(loginUser);

        return loginUser;
    }

    @Override
    public void logout() {
        // TODO: 实现登出逻辑
    }

    @Override
    public String refreshToken(String token) {
        return jwtTokenUtil.refreshToken(token);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, String username) {
        // TODO: 实现登录日志记录
        System.out.println("用户登录：" + username);
    }
}