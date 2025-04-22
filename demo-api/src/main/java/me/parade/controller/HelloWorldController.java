package me.parade.controller;

import jakarta.validation.Valid;
import me.parade.domain.dto.UserCreateParam;
import me.parade.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


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
    public Result<String> hello() {
        return Result.success("Hello World!", "RBAC后台管理系统测试成功");
    }

    @PostMapping("/user/create")
    public Result<String> createUser(@RequestBody @Valid UserCreateParam param) {
        logger.info("用户创建请求: {}", param.getUsername());
        // 正常情况返回成功
        return Result.success("创建用户成功：" + param.getUsername());
    }
}