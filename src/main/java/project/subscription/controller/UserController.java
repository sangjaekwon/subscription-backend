package project.subscription.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.subscription.dto.JoinRequest;
import project.subscription.dto.LoginRequest;
import project.subscription.service.UserService;

@Tag(name = "User API", description = "로그인 및 회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @PostMapping("/join")
    public ResponseEntity<String> joinRequest(@Validated @Parameter(description = "아이디 4~12자, 비밀번호 영어+숫자+특수문자를 포함한 6~16자")
                                                  JoinRequest joinRequest, BindingResult result) {
        if(result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordConfirm())) {
            return ResponseEntity.badRequest().body("비밀번호와 비밀번호확인이 일치하지 않습니다.");
        }

        userService.join(joinRequest);

        return ResponseEntity.status(201).body("회원가입 성공");
    }

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginRequest(@Validated LoginRequest loginRequest, BindingResult result) {
        if(result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
        }

        String token = userService.login(loginRequest);

        return ResponseEntity.ok(token);
    }

}
