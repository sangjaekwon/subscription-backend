package project.subscription.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinRequest {


    @Schema(description = "아이디")
    @NotBlank(message = "아이디를 입력해 주세요.")
    @Pattern(regexp = "^[A-Za-z0-9]{4,12}$", message = "영어(대소문자), 숫자를 포함한 4~12자로 입력해 주세요,")
    private String username;

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,16}$", message = "비밀번호는 영어(대소문자) + 숫자 + 특수문자를 포함한 6~16자로 입력해 주세요.")
    private String password;

    @Schema(description = "비밀번호 확인")
    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordConfirm;


}
