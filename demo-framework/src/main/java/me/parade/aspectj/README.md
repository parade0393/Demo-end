# 日志切面实现说明

## 概述

本项目实现了两种日志切面，用于记录系统中的请求日志和操作日志：

1. **LogAspect**：通用日志切面，自动记录所有Controller方法的请求参数、响应结果和执行时间等信息
2. **OperationLogAspect**：操作日志切面，针对带有`@OperationLog`注解的方法，记录更详细的操作信息，并为后续保存到数据库做准备

## 实现功能

### LogAspect（通用日志切面）

- 自动拦截所有Controller方法
- 记录请求URL、HTTP方法、请求参数、IP地址等信息
- 记录响应结果和执行时间
- 记录异常信息（如果发生）

### OperationLog（操作日志注解）

- 用于标记需要记录详细操作日志的方法
- 可配置操作模块和操作类型
- 可控制是否记录请求参数、响应结果和异常信息

### OperationLogAspect（操作日志切面）

- 拦截带有`@OperationLog`注解的方法
- 记录详细的操作信息，包括操作模块、操作类型等
- 为后续将日志保存到数据库做准备（目前仅打印日志，实际项目中可扩展为保存到数据库）

## 使用方法

### 通用日志记录

无需特殊配置，所有Controller方法都会自动记录日志。

### 操作日志记录

在需要记录详细操作日志的方法上添加`@OperationLog`注解：

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @OperationLog(module = "用户管理", operation = "创建用户")
    @PostMapping
    public Result<UserVO> createUser(@RequestBody @Valid UserCreateParam param) {
        // 创建用户的业务逻辑
        return Result.success(userService.createUser(param));
    }
    
    @OperationLog(module = "用户管理", operation = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        // 删除用户的业务逻辑
        userService.deleteUser(id);
        return Result.success();
    }
}
```

## 后续扩展

1. 实现操作日志保存到数据库的功能
2. 添加异步日志记录，减少对接口性能的影响
3. 实现更细粒度的日志配置，如按模块配置是否记录日志
4. 集成更多信息，如用户ID、用户名等

## 注意事项

1. 敏感信息（如密码）应在日志记录前进行脱敏处理
2. 大型请求或响应数据应考虑截断或不记录，以避免日志过大
3. 生产环境中应配置合适的日志级别，避免过多的日志输出