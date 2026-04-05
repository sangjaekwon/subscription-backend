package project.subscription.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "API 호출에 사용할 Access Token", example = "eyJhbGciOiJIUzI1NiJ9.access-token")
    private String accessToken;
    @Schema(description = "재발급용 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9.refresh-token")
    private String refreshToken;
}
