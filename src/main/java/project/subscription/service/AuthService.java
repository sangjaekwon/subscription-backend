package project.subscription.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.CustomUserPrincipal;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.exception.ex.ExpiredJwtTokenException;
import project.subscription.exception.ex.InvalidJwtTokenException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.jwt.JwtUtil;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (RuntimeException e) {
            throw new UserNotFoundException();
        }
        Long userId = ((CustomUserPrincipal) authenticate.getPrincipal()).getUserId();
        return createToken(String.valueOf(userId));
    }

    public LoginResponse reissue(String refreshToken) {
        try {
            jwtUtil.validate(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtTokenException();
        } catch (JwtException e) {
            throw new InvalidJwtTokenException();
        }

        String userId = jwtUtil.getUserId(refreshToken);
        String type = jwtUtil.getTokenType(refreshToken);

        String savedToken = redisTemplate.opsForValue().get("refresh:" + userId);
        if (savedToken == null || !savedToken.equals(refreshToken) || !"refresh".equals(type)) {
            throw new InvalidJwtTokenException();
        }

        return createToken(userId);

    }

    private LoginResponse createToken(String userId) {
        LoginResponse token = new LoginResponse(jwtUtil.createToken(userId, "access"), jwtUtil.createToken(userId, "refresh"));
        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                token.getRefreshToken(),
                Duration.ofDays(14)
        );
        return token;
    }

    public void logout(String userId) {
        redisTemplate.delete("refresh:" + userId);
        SecurityContextHolder.clearContext();
    }
}
