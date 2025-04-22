package me.parade;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.parade.domain.dto.UserCreateParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest(HelloWorldController.class)
 * 首先，使用 @WebMvcTest 注解来测试控制器类。这个注解会自动配置 Spring MVC 的相关组件，并且只加载指定的控制器类。而且包名也要和控制器类一致。
 * 这样可以避免加载整个应用程序上下文，从而加快测试速度。
 * 但是，这种方式只会加载与控制器相关的 Bean，因此如果你的控制器依赖于其他 Bean（例如服务类），你需要手动 Mock 这些 Bean。也不会加载spring的配置文件。如配置了context-path在这里不会生效。
 * 另外，@WebMvcTest 注解默认会禁用全局异常处理器和拦截器，如果你需要测试这些功能，可以使用 @Import 注解来导入它们。
 * 
 * 注意：如果你使用了 @SpringBootTest 注解来测试整个应用程序(会注册所有带注解的bean)，那么你需要使用 MockMvcBuilders 来创建 MockMvc 对象，并且需要手动配置 ServletContext 和 WebApplicationContext。
 * 这样会比较麻烦，而且测试速度也会变慢。
 * @SpringBootTest 会加载完整的 Spring Boot 应用（包括 application.yml 配置），context-path 等配置会生效。
 * @AutoConfigureMockMvc 自动配置 MockMvc，无需手动创建。
 * 测试请求路径应与实际部署一致（如 /api/user/create）。适合集成测试，启动速度比 @WebMvcTest 慢，但更贴近真实环境。
 * MockMvc已经自动配置了应用的context-path。这意味着测试请求路径不应该包含context-path前缀，因为MockMvc会自动处理这个前缀。
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_invalidParam_shouldReturnBadRequest() throws Exception {
        UserCreateParam param = new UserCreateParam();
        param.setUsername(""); // 为空
        param.setEmail("not-an-email");
        param.setPassword("123");

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_validParam_shouldReturnSuccess() throws Exception {
        UserCreateParam param = new UserCreateParam();
        param.setUsername("zhangsan");
        param.setEmail("zhangsan@example.com");
        param.setPassword("123456");

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("创建用户成功")));
    }
}
