package project.subscription.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.JoinRequest;
import project.subscription.dto.LoginRequest;
import project.subscription.entity.User;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.DuplicateUsernameException;
import project.subscription.jwt.JwtUtil;
import project.subscription.repository.UserRepository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void 회원가입_정상() throws Exception {
        //given
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUsername("sangjae");
        joinRequest.setPassword("abc123!@#");
        joinRequest.setPasswordConfirm("abc123!@#");

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
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setUsername("sangjae");
        joinRequest.setPassword("abc123!@#");
        joinRequest.setPasswordConfirm("abc123!@#");
        //회원가입2
        JoinRequest joinRequest2 = new JoinRequest();
        joinRequest2.setUsername("sangjae"); //아이디 중복
        joinRequest2.setPassword("abc123!@#");
        joinRequest2.setPasswordConfirm("abc123!@#");

        //when
        userService.join(joinRequest);

        //then
        assertThatThrownBy(() -> userService.join(joinRequest2)).
                isInstanceOf(DuplicateUsernameException.class);
    }

    @Test
    public void 로그인_정상() throws Exception {
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
        assertThat(jwtUtil.getUsername(token)).isEqualTo(loginRequest.getUsername());
    }
    @Test
    public void 로그인_예외_존재하지않는유저() throws Exception {
        //given
        //회원가입 생략
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("sangjae");
        loginRequest.setPassword("abc123!@#");

        //then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BadLoginException.class);
    }
}