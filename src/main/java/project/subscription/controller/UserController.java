package project.subscription.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.subscription.dto.request.JoinRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.dto.response.LoginResponse;
import project.subscription.service.UserService;

import java.time.Duration;

@Tag(name = "User API", description = "회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류")
    })
    @PostMapping("/join")
    public ResponseEntity<CommonApiResponse<?>> join(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "아이디 4~12자, 비밀번호 영어+숫자+특수문자를 포함한 6~16자")
                                                            @RequestBody @Validated JoinRequest joinRequest) {
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordConfirm())) {
            return ResponseEntity.badRequest().body(CommonApiResponse.error("비밀번호와 비밀번호확인이 일치하지 않습니다."));
        }

        userService.join(joinRequest);

        return ResponseEntity.status(201).body(CommonApiResponse.ok(null));
    }


}
