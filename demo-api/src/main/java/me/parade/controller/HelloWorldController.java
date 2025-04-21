package me.parade.controller;

import me.parade.domain.dto.UserCreateParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
public class HelloWorldController {
    Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    /**
     * 测试接口
     * @return 测试数据
     */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        logger.info("HelloWorldController init");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "Hello World!");
        result.put("data", "RBAC后台管理系统测试成功");
        return result;
    }

    @PostMapping("/user/create")
    public String createUser(@RequestBody @Valid UserCreateParam param) {
        // 正常情况返回成功
        return "创建用户成功：" + param.getUsername();
    }
}