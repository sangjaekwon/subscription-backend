package project.subscription.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import project.subscription.dto.CustomUserPrincipal;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();
        String code = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set(
                "oauth2:" + code, String.valueOf(user.getUserId()), Duration.ofSeconds(30L));

        response.sendRedirect("https://xn--zb0b0h61ozwg9lg1uep6c.sited/oauth2?code=" + code);
    }
}
