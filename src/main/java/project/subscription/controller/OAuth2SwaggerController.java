package project.subscription.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name= "OAuth2 소셜 로그인", description = "소셜 로그인 엔드포인트")
@RestController
public class OAuth2SwaggerController {

    @Operation(
            summary = "Google OAuth2 로그인",
            description = """
        Google OAuth2 로그인 시작 엔드포인트<br>
        실제 처리는 Spring Security OAuth2가 담당하며,
        호출 시 Google 로그인 페이지로 리다이렉트됩니다.
        """
    )
    @GetMapping("/oauth2/authorization/google")
    public void googleLogin() {
        throw new UnsupportedOperationException("Swagger 문서용 엔드포인트입니다.");
    }

    @Operation(
            summary = "Google OAuth2 로그인",
            description = """
        Google OAuth2 로그인 시작 엔드포인트<br>
        실제 처리는 Spring Security OAuth2가 담당하며,
        호출 시 Google 로그인 페이지로 리다이렉트됩니다.
        """
    )
    @GetMapping("/oauth2/authorization/naver")
    public void naverLogin() {
        throw new UnsupportedOperationException("Swagger 문서용 엔드포인트입니다.");
    }
}
