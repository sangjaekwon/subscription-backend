package project.subscription.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThat(jwtUtil.getUserId(token)).isEqualTo("sangjae");
        assertThat(jwtUtil.getTokenType(token)).isEqualTo("access");

    }

    @Test
    public void JWT토큰_예외_만료된토큰() throws Exception {
        //given
        String accessToken = jwtUtil.createToken("sangjae", "access"); // 테스트는 access 토큰 만료 20초
        String refreshToken = jwtUtil.createToken("sangjae2", "refresh"); // 테스트는 refresh 토큰 만료 25초

        //when
        Thread.sleep(2501);


        //then
        assertThatThrownBy(() -> jwtUtil.validate(accessToken)).isInstanceOf(ExpiredJwtException.class);
        assertThatThrownBy(() -> jwtUtil.validate(refreshToken)).isInstanceOf(ExpiredJwtException.class);

    }

    @Test
    public void JWT필터_정상() throws Exception {
        //given
        String token = jwtUtil.createToken("99999999", "access");
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