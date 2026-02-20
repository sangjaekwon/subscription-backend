package project.subscription.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VerifyEmailCodeRequest {

    @Email
    private String email;
    @Min(value = 100000,message = "인증 코드는 6자리 정수로 입력해 주세요")
    @Max(value = 999999,message = "인증 코드는 6자리 정수로 입력해 주세요")
    private int code;
}
