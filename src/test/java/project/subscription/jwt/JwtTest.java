package project.subscription.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project.subscription.dto.request.JoinRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.service.UserService;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class JwtTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void JWT토큰_발행_검증() throws Exception {
        //given
        String token = jwtUtil.createToken("sangjae", "access");

        //when
        //then
        jwtUtil.validate(token);
        assertThat(jwtUtil.getUsername(token)).isEqualTo("sangjae");
        assertThat(jwtUtil.getType(token)).isEqualTo("access");

    }

    @Test
    public void JWT필터_정상() throws Exception {
        //given
        String token = jwtUtil.createToken("sangjae", "access");
        //when

        //then
        mockMvc.perform(get("/")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

    }

    @Test
    public void JWT필터_예외_잘못된토큰() throws Exception {
        //given
        String token = "fake-token";

        //then
        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

}