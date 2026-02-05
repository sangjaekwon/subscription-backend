package project.subscription.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.request.JoinRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.InvalidJwtTokenException;
import project.subscription.jwt.JwtUtil;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Test
    public void 로그인_정상() throws Exception {
        //given
        JoinRequest joinRequest = createJoinRequest();
        userService.join(joinRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(joinRequest.getUsername());
        loginRequest.setPassword("abc123!@#");

        //when
        LoginResponse token = authService.login(loginRequest);

        //then
        assertThat(redisTemplate.opsForValue().get("refresh:" + loginRequest.getUsername()))
                .isEqualTo(token.getRefreshToken()); // redis 검증
        assertThat(jwtUtil.getUsername(token.getAccessToken())).isEqualTo(loginRequest.getUsername());
        assertThat(jwtUtil.getType(token.getAccessToken())).isEqualTo("access");
        assertThat(jwtUtil.getUsername(token.getRefreshToken())).isEqualTo(loginRequest.getUsername());
        assertThat(jwtUtil.getType(token.getRefreshToken())).isEqualTo("refresh");
    }

    @Test
    public void 로그인_예외_존재하지않는유저() throws Exception {
        //given
        //회원가입 생략
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sangjae");
        loginRequest.setPassword("abc123!@#");

        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadLoginException.class);
    }

    @Test
    public void 리프레시토큰_재발급_정상() throws Exception {
        //given
        JoinRequest joinRequest = createJoinRequest();
        userService.join(joinRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(joinRequest.getUsername());
        loginRequest.setPassword("abc123!@#");

        //when
        LoginResponse token = authService.login(loginRequest);
        //when

        LoginResponse reissueToken = authService.reissue(token.getRefreshToken());
        //then
        assertThat(jwtUtil.getUsername(reissueToken.getAccessToken()))
                .isEqualTo(jwtUtil.getUsername(token.getAccessToken()));
        assertThat(jwtUtil.getType(reissueToken.getAccessToken()))
                .isEqualTo(jwtUtil.getType(token.getAccessToken()));

    }
    @Test
    public void 리프레시토큰_재발급_예외_유효하지않은토큰() throws Exception {
        //given
        String token = "fake-token";

        //then
        assertThatThrownBy(() -> authService.reissue(token)).isInstanceOf(InvalidJwtTokenException.class);

    }

    @Test
    public void 로그아웃_정상() throws Exception {
        //given
        JoinRequest joinRequest = createJoinRequest();
        userService.join(joinRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(joinRequest.getUsername());
        loginRequest.setPassword("abc123!@#");

        //when
        authService.login(loginRequest);
        authService.logout(joinRequest.getUsername());

        //then
        assertThat(redisTemplate.opsForValue().get("refresh:" + joinRequest.getUsername()))
                .isNull();
    }

    private static JoinRequest createJoinRequest() {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUsername(UUID.randomUUID().toString()); // redis 저장시 개발서버와 겹칠 수 있기에
        joinRequest.setPassword("abc123!@#");
        joinRequest.setPasswordConfirm("abc123!@#");
        return joinRequest;
    }

}