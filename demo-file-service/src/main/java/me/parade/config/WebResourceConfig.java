package me.parade.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebResourceConfig implements WebMvcConfigurer {

    private final FileUploadProperties fileUploadProperties;

    @Override
    /**
     * 添加静态资源处理器
     * 将本地文件系统中的上传目录映射到Web路径/files/下
     * 前端访问如：http://localhost:8080/api/files/b/e/be046dae929e4681a938577eeac3396b.jpg
     * 使用fileUploadProperties中配置的当前基础路径作为资源位置
     * @param registry 资源处理器注册表
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，用于访问上传的文件
        String basePath = fileUploadProperties.getCurrentBasePath();
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + basePath);
    }
}
