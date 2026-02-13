package project.subscription.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.subscription.dto.CustomUserPrincipal;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.dto.response.LoginResponse;
import project.subscription.jwt.JwtUtil;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();

        String access = jwtUtil.createToken(userPrincipal.getUsername(), "access");
        String refresh = jwtUtil.createToken(userPrincipal.getUsername(), "refresh");

        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refreshToken", refresh)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .maxAge(Duration.ofDays(14))
                        .build().toString()
        );

        redisTemplate.opsForValue().set("refresh:" + userPrincipal.getUsername(), refresh);

        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(CommonApiResponse.ok(new LoginResponse(access, refresh)))
        );

    }
}
