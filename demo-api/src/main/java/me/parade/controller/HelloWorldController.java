package me.parade.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.parade.annotation.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 测试控制器
 * Tag注解，标识控制器的名称和描述
 */
@RestController
@Tag(name = "测试接口", description = "用于测试系统基本功能的接口")
public class HelloWorldController {
    Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    /**
     * 测试接口
     * Operation注解，提供接口摘要和详细描述
     * @return 测试数据
     */
    @Operation(summary = "Hello World测试", description = "返回一个简单的Hello World消息，用于测试系统是否正常运行")
    @ResponseResult
    @GetMapping("/hello")
    public String hello() {
//        throw new ApiException(401, ResultCode.VALIDATE_FAILED, "RBAC后台管理系统测试失败");
//        UserCreateParam userCreateParam = new UserCreateParam();
//        userCreateParam.setUsername("parade");
//        userCreateParam.setPassword("123456");
//        userCreateParam.setPassword("111");
//        return userCreateParam;
        return "测试成功";
//        return Result.success("Hello World!", "RBAC后台管理系统测试成功");
    }
}