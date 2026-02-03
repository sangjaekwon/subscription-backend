package project.subscription.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {


    @Schema(description = "아이디")
    @NotBlank(message = "아이디를 입력해 주세요.")
    private String username;

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
