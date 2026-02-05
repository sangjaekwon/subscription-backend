package project.subscription.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.request.JoinRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.entity.User;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.DuplicateUsernameException;
import project.subscription.jwt.JwtUtil;
import project.subscription.repository.UserRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Test
    public void 회원가입_정상() throws Exception {
        //given
        JoinRequest joinRequest = createJoinRequest();

        //when
        userService.join(joinRequest);
        User user = userRepository.findByUsername(joinRequest.getUsername());
        //then
        assertThat(joinRequest.getUsername()).isEqualTo(user.getUsername());
        assertThat(encoder.matches("abc123!@#", user.getPassword())).isTrue();
    }

    @Test
    public void 회원가입_예외_아이디중복() throws Exception {
        //given
        //회원가입1
        JoinRequest joinRequest = createJoinRequest();
        //회원가입2
        JoinRequest joinRequest2 = new JoinRequest();
        joinRequest2.setUsername(joinRequest.getUsername());
        joinRequest2.setPassword("abc123!@#");
        joinRequest2.setPasswordConfirm("abc123!@#");

        //when
        userService.join(joinRequest);

        //then
        assertThatThrownBy(() -> userService.join(joinRequest2)).
                isInstanceOf(DuplicateUsernameException.class);
    }

    private static JoinRequest createJoinRequest() {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUsername(UUID.randomUUID().toString()); // redis 저장시 개발서버와 겹칠 수 있기에
        joinRequest.setPassword("abc123!@#");
        joinRequest.setPasswordConfirm("abc123!@#");
        return joinRequest;
    }
}