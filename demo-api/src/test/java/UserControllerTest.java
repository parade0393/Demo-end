import com.fasterxml.jackson.databind.ObjectMapper;
import me.parade.controller.HelloWorldController;
import me.parade.domain.dto.UserCreateParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloWorldController.class)
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

        mockMvc.perform(post("/api/users")
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

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("创建用户成功")));
    }
}
