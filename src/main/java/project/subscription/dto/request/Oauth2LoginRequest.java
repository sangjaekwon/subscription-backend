package project.subscription.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Oauth2LoginRequest {

    @Schema(description = "OAuth2 로그인 성공 후 백엔드가 발급한 1회성 code", example = "oauth2-login-code-123456")
    private String code;
}
