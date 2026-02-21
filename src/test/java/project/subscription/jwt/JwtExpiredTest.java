package project.subscription.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;



@SpringBootTest
@TestPropertySource(properties = {
        "jwt.access-expiration=1000",
        "jwt.refresh-expiration=1000"
}) // 만료 측정을 위해 토큰 1초로 변경
public class JwtExpiredTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void JWT토큰_예외_만료된토큰() throws Exception {
        //given
        String accessToken = jwtUtil.createToken("sangjae", "access"); // access 토큰 만료 1초
        String refreshToken = jwtUtil.createToken("sangjae2", "refresh"); // refresh 토큰 만료 1초

        //when
        Thread.sleep(1000);


        //then
        assertThatThrownBy(() -> jwtUtil.validate(accessToken)).isInstanceOf(ExpiredJwtException.class);
        assertThatThrownBy(() -> jwtUtil.validate(refreshToken)).isInstanceOf(ExpiredJwtException.class);

    }
}
