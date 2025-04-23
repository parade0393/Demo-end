package me.parade.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import me.parade.domain.common.BaseEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 代码生成器 - 仅生成实体类
 */
public class CodeGenerator {
    // 数据库连接配置
    private static final String URL = "jdbc:mysql://localhost:3306/rbac_admin?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        // 测试数据库连接
        try {
            System.out.println("正在测试数据库连接...");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("数据库连接成功！");
            conn.close();
        } catch (SQLException e) {
            System.err.println("数据库连接失败！错误信息：" + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 项目根路径
        String projectPath = System.getProperty("user.dir");
        System.out.println("项目根路径：" + projectPath);

        System.out.println("开始生成实体类...");

        // 代码生成器配置
        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("parade") // 设置作者
                            .commentDate("yyyy-MM-dd HH:mm:ss") // 注释日期格式
                            .outputDir(projectPath + "/demo-system/src/main/java") // 指定输出目录
                            .disableOpenDir(); // 禁止打开输出目录
                    System.out.println("输出目录：" + projectPath + "/demo-system/src/main/java");
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("me.parade") // 设置父包名
                            .moduleName("") // 设置父包模块名
                            .entity("domain.entity"); // 设置entity包名
                })
                // 策略配置
                .strategyConfig(builder -> {
                    // 实体类策略配置
                    builder.entityBuilder()
                            .superClass(BaseEntity.class) // 设置父类
                            .enableLombok() // 开启lombok
                            .enableTableFieldAnnotation() // 开启生成实体时生成字段注解
                            .naming(NamingStrategy.underline_to_camel) // 数据库表映射到实体的命名策略
                            .columnNaming(NamingStrategy.underline_to_camel) // 数据库表字段映射到实体的命名策略
                            .addSuperEntityColumns("id", "create_time", "update_time", "create_by", "update_by", "version", "del_flag") // 添加父类公共字段
                            .formatFileName("%s"); // 格式化文件名称
                })
                // 模板配置
                .templateConfig(builder -> {
                    // 禁用所有模板
                    builder.disable();
                    // 只启用实体类模板
                    builder.entity("/templates/entity.java");
                    // 禁用其他所有模板
                    builder.controller(null)
                            .service(null)
                            .serviceImpl(null)
                            .mapper(null)
                            .xml(null);
                })
                // 使用Velocity引擎模板，重要：这能确保实体类生成的格式正确
                .templateEngine(new VelocityTemplateEngine())
                // 执行代码生成
                .execute();

        System.out.println("实体类生成完成！");
    }
}