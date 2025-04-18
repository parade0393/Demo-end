## 项目目录机构
```
rbac-admin/
├── pom.xml                                    # 父模块POM，管理依赖版本
├── rbac-common/                               # 通用模块
│   ├── pom.xml
│   └── src/main/java/com/example/common/
│       ├── annotation/                        # 自定义注解
│       ├── constant/                          # 常量定义
│       ├── enums/                             # 枚举类
│       ├── exception/                         # 全局异常类
│       │   ├── BusinessException.java         # 业务异常
│       │   ├── GlobalExceptionHandler.java    # 全局异常处理器
│       │   └── ApiException.java              # API异常
│       ├── result/                            # 统一返回结果
│       │   ├── Result.java                    # 统一返回对象
│       │   └── ResultCode.java                # 结果状态码
│       └── utils/                             # 工具类
│           ├── DateUtils.java                 # 日期工具
│           ├── StringUtils.java               # 字符串工具
│           ├── SecurityUtils.java             # 安全相关工具
│           ├── RSAUtils.java                  # RSA加密工具
│           ├── AESUtils.java                  # AES加密工具
│           └── RedisUtils.java                # Redis工具类
├── rbac-framework/                            # 框架核心模块
│   ├── pom.xml
│   └── src/main/java/com/example/framework/
│       ├── config/                            # 框架配置
│       │   ├── RedisConfig.java               # Redis配置
│       │   ├── MyBatisPlusConfig.java         # MyBatis-Plus配置
│       │   ├── WebMvcConfig.java              # Spring MVC配置
│       │   ├── SpringDocConfig.java           # API文档配置
│       │   └── ThreadPoolConfig.java          # 线程池配置
│       ├── security/                          # 安全框架
│       │   ├── config/                        # 安全配置
│       │   │   ├── SecurityConfig.java        # Spring Security配置
│       │   │   └── JwtConfig.java             # JWT配置
│       │   ├── filter/                        # 过滤器
│       │   │   ├── JwtAuthenticationFilter.java  # JWT认证过滤器
│       │   │   └── RateLimitFilter.java       # 接口限流过滤器
│       │   ├── handler/                       # 处理器
│       │   │   ├── AccessDeniedHandlerImpl.java  # 访问拒绝处理器
│       │   │   └── AuthenticationEntryPointImpl.java  # 认证失败处理器
│       │   └── service/                       # 安全服务
│       │       ├── UserDetailsServiceImpl.java  # 用户详情服务
│       │       └── TokenService.java          # 令牌服务
│       ├── aspectj/                           # AOP切面
│       │   ├── LogAspect.java                 # 日志切面
│       │   └── RateLimitAspect.java           # 限流切面
│       ├── cache/                             # 缓存管理
│       │   ├── CacheManager.java              # 缓存管理器
│       │   └── PermissionCache.java           # 权限缓存
│       └── web/                               # Web相关
│           ├── interceptor/                   # 拦截器
│           └── servlet/                       # Servlet扩展
├── rbac-system/                               # 系统功能模块
│   ├── pom.xml
│   └── src/main/java/com/example/system/
│       ├── domain/                            # 领域模型
│       │   ├── entity/                        # 实体类
│       │   │   ├── SysUser.java               # 用户实体
│       │   │   ├── SysRole.java               # 角色实体
│       │   │   ├── SysMenu.java               # 菜单实体
│       │   │   ├── SysPermission.java         # 权限实体
│       │   │   ├── SysUserRole.java           # 用户角色关联
│       │   │   ├── SysRoleMenu.java           # 角色菜单关联
│       │   │   ├── SysRolePermission.java     # 角色权限关联
│       │   │   ├── SysDept.java               # 部门实体
│       │   │   ├── SysPost.java               # 岗位实体
│       │   │   ├── SysLoginLog.java           # 登录日志
│       │   │   ├── SysOperLog.java            # 操作日志
│       │   │   └── SysConfig.java             # 系统配置
│       │   ├── dto/                           # 数据传输对象
│       │   │   ├── UserDTO.java               # 用户DTO
│       │   │   ├── RoleDTO.java               # 角色DTO
│       │   │   └── MenuDTO.java               # 菜单DTO
│       │   └── vo/                            # 视图对象
│       │       ├── UserVO.java                # 用户视图
│       │       ├── RoleVO.java                # 角色视图
│       │       ├── MenuVO.java                # 菜单视图
│       │       └── PermissionVO.java          # 权限视图
│       ├── mapper/                            # MyBatis Mapper
│       │   ├── SysUserMapper.java             # 用户Mapper
│       │   ├── SysRoleMapper.java             # 角色Mapper
│       │   ├── SysMenuMapper.java             # 菜单Mapper
│       │   ├── SysPermissionMapper.java       # 权限Mapper
│       │   └── xml/                           # XML映射文件
│       │       ├── SysUserMapper.xml          # 用户Mapper XML
│       │       ├── SysRoleMapper.xml          # 角色Mapper XML
│       │       └── SysMenuMapper.xml          # 菜单Mapper XML
│       ├── service/                           # 服务层
│       │   ├── SysUserService.java            # 用户服务接口
│       │   ├── SysRoleService.java            # 角色服务接口
│       │   ├── SysMenuService.java            # 菜单服务接口
│       │   ├── SysPermissionService.java      # 权限服务接口
│       │   ├── impl/                          # 服务实现
│       │       ├── SysUserServiceImpl.java    # 用户服务实现
│       │       ├── SysRoleServiceImpl.java    # 角色服务实现
│       │       ├── SysMenuServiceImpl.java    # 菜单服务实现
│       │       └── SysPermissionServiceImpl.java  # 权限服务实现
│       └── controller/                        # 控制器
│           ├── SysUserController.java         # 用户控制器
│           ├── SysRoleController.java         # 角色控制器
│           ├── SysMenuController.java         # 菜单控制器
│           └── SysLoginController.java        # 登录控制器
├── rbac-job/                                  # 任务调度模块
│   ├── pom.xml
│   └── src/main/java/com/example/job/
│       ├── domain/                            # 任务实体
│       ├── mapper/                            # 任务Mapper
│       ├── service/                           # 任务服务
│       ├── util/                              # 任务工具
│       └── controller/                        # 任务控制器
├── rbac-generator/                            # 代码生成模块
│   ├── pom.xml
│   └── src/main/java/com/example/generator/
│       ├── domain/                            # 生成相关实体
│       ├── service/                           # 生成服务
│       ├── util/                              # 生成工具
│       └── controller/                        # 生成控制器
└── rbac-api/                                  # API入口模块
    ├── pom.xml
    ├── src/main/java/com/example/
    │   ├── RbacAdminApplication.java          # 启动类
    │   └── controller/                        # API控制器
    ├── src/main/resources/
    │   ├── application.yml                    # 主配置文件
    │   ├── application-dev.yml                # 开发环境配置
    │   ├── application-prod.yml               # 生产环境配置
    │   ├── logback-spring.xml                 # 日志配置
    │   └── mapper/                            # MyBatis映射文件
    └── src/test/                              # 测试目录
        └── java/com/example/
            └── controller/                    # 控制器测试
```