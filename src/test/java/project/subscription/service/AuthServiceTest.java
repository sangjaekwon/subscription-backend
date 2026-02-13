package project.subscription.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.request.JoinRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.entity.User;
import project.subscription.exception.ex.InvalidJwtTokenException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.jwt.JwtUtil;
import project.subscription.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
    @Autowired
    private UserRepository userRepository;

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
        User user = userRepository.findByUsername(joinRequest.getUsername()).get();

        //then
        assertThat(jwtUtil.getUserId(token.getAccessToken())).isEqualTo(String.valueOf(user.getId()));
        assertThat(jwtUtil.getTokenType(token.getAccessToken())).isEqualTo("access");
        assertThat(jwtUtil.getUserId(token.getRefreshToken())).isEqualTo(String.valueOf(user.getId()));
        assertThat(jwtUtil.getTokenType(token.getRefreshToken())).isEqualTo("refresh");
        assertThat(redisTemplate.opsForValue().get("refresh:" + user.getId()))
                .isEqualTo(token.getRefreshToken()); // redis 검증
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
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void 로그인_예외_비밀번호불일치() throws Exception {
        //given
        //회원가입 생략
        JoinRequest joinRequest = createJoinRequest();
        userService.join(joinRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(joinRequest.getUsername());

        //when
        loginRequest.setPassword("wrong-password");

        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class);
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

        LoginResponse reissueToken = authService.reissue(token.getRefreshToken());

        //then
        assertThat(jwtUtil.getUserId(reissueToken.getAccessToken()))
                .isEqualTo(jwtUtil.getUserId(token.getAccessToken()));
        assertThat(jwtUtil.getTokenType(reissueToken.getAccessToken()))
                .isEqualTo(jwtUtil.getTokenType(token.getAccessToken()));

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
        User user = userRepository.findByUsername(joinRequest.getUsername()).get();

        authService.logout(String.valueOf(user.getId()));


        //then
        assertThat(redisTemplate.opsForValue().get("refresh:" + user.getId()))
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