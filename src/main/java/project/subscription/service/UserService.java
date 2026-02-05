package project.subscription.service;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.request.JoinRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.entity.User;
import project.subscription.exception.ex.BadLoginException;
import project.subscription.exception.ex.DuplicateUsernameException;
import project.subscription.jwt.JwtUtil;
import project.subscription.repository.UserRepository;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public void join(JoinRequest joinRequest) {
        User user = userRepository.findByUsername(joinRequest.getUsername());
        if(user != null) throw new DuplicateUsernameException();

        userRepository.save(new User(joinRequest.getUsername(), encoder.encode(joinRequest.getPassword()), "ROLE_USER"));
    }


}
