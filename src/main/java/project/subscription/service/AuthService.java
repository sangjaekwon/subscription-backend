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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import project.subscription.dto.CustomUserPrincipal;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.request.Oauth2LoginRequest;
import project.subscription.dto.response.LoginResponse;
import project.subscription.exception.ex.*;
import project.subscription.jwt.JwtUtil;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MailService mailService;
    private final TemplateEngine templateEngine;

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

    public void createEmailCode(String email) {
        int emailCode = ThreadLocalRandom.current().nextInt(100000, 1000000);

        Context context = new Context();
        context.setVariable("code", emailCode);
        String html = templateEngine.process("email-code", context);

        mailService.sendVerifyMail(email, html);

        redisTemplate.opsForValue().set("emailCode:" + email, String.valueOf(emailCode), Duration.ofMinutes(5));
    }

    public void verifyEmailCode(String email, int code) {
        String key = "emailCode:" + email;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            throw new ExpiredEmailCodeException();
        }
        if (!savedCode.equals(String.valueOf(code))) {
            throw new InvalidEmailCodeException();
        }

        redisTemplate.delete(key);
        redisTemplate.opsForValue().set("email:verification:" + email, "true", Duration.ofMinutes(30));
    }

    public LoginResponse loginOauth2(Oauth2LoginRequest loginRequest) {
        String key = "oauth2:" + loginRequest.getCode();
        String userId = redisTemplate.opsForValue().get(key);
        if(userId == null) {
            throw new InvalidOauth2CodeException();
        }

        redisTemplate.delete(key);

        return createToken(userId);
    }
}
