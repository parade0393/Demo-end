
# SpringDoc分组配置详解

在`application.yml`文件中，`springdoc.group-configs`配置用于定义API文档的分组，这对于大型项目特别有用，可以将API按功能或模块进行分类展示。以下是对配置的详细解释：

## 分组配置结构

```yaml
springdoc:
  group-configs:
    - group: 'default'      # 分组名称
      paths-to-match: '/**' # 匹配的URL路径模式
      packages-to-scan: ${app.base-package} # 要扫描的包
```

## 配置项说明

1. **group**：分组的名称，在Swagger UI界面上会显示为下拉选项，用户可以切换不同的分组查看对应的API。

2. **paths-to-match**：URL路径匹配模式，用于筛选哪些API路径应该包含在该分组中。
   - `/**`表示匹配所有路径
   - 也可以使用更具体的模式，如`/api/user/**`只匹配用户相关API

3. **packages-to-scan**：要扫描的包路径，只有这些包中的控制器才会被包含在该分组中。
   - 这里使用了`${app.base-package}`变量引用，即`me.parade`
   - 这种方式使配置更加灵活，当包名变更时只需修改一处

## 多分组配置示例

可以添加多个分组，例如：

```yaml
springdoc:
  group-configs:
    - group: 'user-api'
      paths-to-match: '/api/user/**'
      packages-to-scan: ${app.base-package}.user
    - group: 'system-api'
      paths-to-match: '/api/system/**'
      packages-to-scan: ${app.base-package}.system
```

## 分组的优势

1. **提高文档可读性**：将API按功能或模块分组，使文档结构更清晰
2. **便于团队协作**：不同团队可以关注各自负责的API分组
3. **简化API测试**：测试人员可以只关注特定分组的API进行测试
4. **权限控制**：可以结合安全配置，对不同分组应用不同的访问权限

在当前配置中，只有一个默认分组，它包含了`me.parade`包下的所有API。如果项目规模扩大，建议按模块或功能创建多个分组，使API文档更加结构化和易于使用。