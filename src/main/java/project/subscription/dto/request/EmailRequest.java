package project.subscription.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequest {

    @Schema(description = "인증 코드를 받을 이메일 주소", example = "sangjae@example.com")
    @Email
    private String email;
}
