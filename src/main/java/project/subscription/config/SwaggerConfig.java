package project.subscription.config;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class SwaggerConfig {

    @Bean
    @Profile(value = "prod")
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Subscription Backend API")
                        .version("v1")
                        .description("""
                                구독 관리 서비스 백엔드 API 문서입니다.

                                인증 안내
                                - Access Token: Authorization 헤더의 Bearer 토큰으로 전달합니다.
                                - Refresh Token: 로그인/재발급 시 HttpOnly 쿠키로 발급됩니다.
                                - 보호된 API는 Swagger 우측 상단 Authorize 버튼으로 Access Token을 설정한 뒤 호출합니다.
                                """)
                        .contact(new Contact()
                                .name("Subscription Backend")
                                .url("https://github.com/sangjaekwon/subscription-backend")))
                .addServersItem(new Server()
                        .url("https://xn--zb0b0h61ozwg9lg1uep6c.site"));
    }
}
