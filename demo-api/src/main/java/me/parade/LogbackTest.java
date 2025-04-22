package me.parade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Logback日志框架测试类
 * 用于验证日志配置是否生效
 * 这个测试类已经配置为Spring组件(@Component)
 * 当SpringBoot应用启动时会自动执行run方法
 * 只需要确保：
 *  1. 项目中包含logback相关依赖(spring-boot-starter-logging)
 *  2. resources目录下配置logback-spring.xml或application.properties中配置日志级别
 *  3. 直接运行SpringBoot主应用类即可看到不同级别的日志输出
 *  4. 默认情况下TRACE和DEBUG级别不会显示,需要在配置文件中调整日志级别
 */
@Component
public class LogbackTest implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    @Override
    public void run(String... args) {
        // 测试不同级别的日志
        logger.trace("这是 TRACE 级别的日志");
        logger.debug("这是 DEBUG 级别的日志");
        logger.info("这是 INFO 级别的日志");
        logger.warn("这是 WARN 级别的日志");
        logger.error("这是 ERROR 级别的日志");
        
//        // 测试带异常的日志
//        try {
//            // 模拟一个异常
//            int result = 1 / 0;
//        } catch (Exception e) {
//            logger.error("发生异常：", e);
//        }
        
        logger.info("日志框架配置测试完成！");
    }
}