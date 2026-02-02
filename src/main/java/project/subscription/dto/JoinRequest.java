package project.subscription.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinRequest {


    @NotBlank(message = "아이디를 입력해 주세요.")
    @Pattern(regexp = "^[A-Za-z0-9]{4,12}$", message = "영어(대소문자), 숫자를 포함한 4~12자로 입력해 주세요,")
    private String username;
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,14}$", message = "비밀번호는 영어(대소문자) + 숫자 + 특수문자를 포함한 6~14자로 입력해 주세요.")
    private String password;
    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String passwordConfirm;

}
