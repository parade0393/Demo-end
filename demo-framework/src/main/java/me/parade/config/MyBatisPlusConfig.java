package me.parade.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;

@Configuration
@MapperScan("me.parade.**.mapper")
public class MyBatisPlusConfig {

    private final Environment environment;

    public MyBatisPlusConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * 配置MybatisPlus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件
//        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 如果是开发环境，添加性能分析插件
//        String[] activeProfiles = environment.getActiveProfiles();
//        for (String profile : activeProfiles) {
//            if ("dev".equals(profile)) {
//                interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
//                break;
//            }
//        }
        return interceptor;
    }

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
                // TODO: 当实现了用户认证后，替换为实际的用户ID
//                this.strictInsertFill(metaObject, "createBy", () -> 1L, Long.class);
//                this.strictInsertFill(metaObject, "updateBy", () -> 1L, Long.class);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
                // TODO: 当实现了用户认证后，替换为实际的用户ID
//                this.strictUpdateFill(metaObject, "updateBy", () -> 1L, Long.class);
            }
        };
    }
}