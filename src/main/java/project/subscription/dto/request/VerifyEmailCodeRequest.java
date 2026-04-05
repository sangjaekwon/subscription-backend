package project.subscription.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyEmailCodeRequest {

    @Schema(description = "인증을 요청한 이메일 주소", example = "sangjae@example.com")
    @Email
    private String email;
    @Schema(description = "이메일로 전송된 6자리 인증 코드", example = "123456")
    @Min(value = 100000,message = "인증 코드는 6자리 정수로 입력해 주세요")
    @Max(value = 999999,message = "인증 코드는 6자리 정수로 입력해 주세요")
    private int code;
}
