package project.subscription.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import project.subscription.dto.JoinRequest;
import project.subscription.dto.LoginRequest;
import project.subscription.service.UserService;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class JwtTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Test
    public void JWT토큰_발행_검증() throws Exception {
        //given
        String token = jwtUtil.createToken("sangjae");

        //when
        //then
        jwtUtil.validate(token);
        assertThat(jwtUtil.getUsername(token)).isEqualTo("sangjae");

    }

    @Test
    public void JWT필터_정상() throws Exception {
        //given
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUsername("sangjae");
        joinRequest.setPassword("abc123!@#");
        joinRequest.setPasswordConfirm("abc123!@#");
        userService.join(joinRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sangjae");
        loginRequest.setPassword("abc123!@#");
        //when
        String token = userService.login(loginRequest);

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