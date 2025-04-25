package me.parade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 如果某个全局bean产生循环依赖，可以单独放在这里
 * 把这里当成一个配置中心
 */
@Configuration
public class GlobalBeanConfig {
    /**
     * 密码编码器
     * 使用BCrypt强哈希算法进行密码加密
     * 单独放在这里，是为了避免产生循环依赖
     * @return PasswordEncoder 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
