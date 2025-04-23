package me.parade.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * SpringDoc配置类
 * 用于配置API文档相关信息
 * 仅在非生产环境（dev、test）下启用
 */
@Configuration
@Profile({"dev", "test"})
@ConditionalOnProperty(
        name = {"springdoc.api-docs.enabled", "springdoc.swagger-ui.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class SpringDocConfig {

    /**
     * 配置OpenAPI信息
     * @return OpenAPI对象
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("RBAC后台管理系统API文档")
                        .description("基于RBAC的后台管理系统，提供用户、角色、权限等管理功能")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com")
                                .url("https://github.com/example/rbac-admin"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://github.com/example/rbac-admin/wiki"));
    }
}