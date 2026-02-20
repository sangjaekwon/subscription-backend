package project.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.subscription.dto.request.EmailRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.request.VerifyEmailCodeRequest;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.dto.response.LoginResponse;
import project.subscription.service.AuthService;

import java.time.Duration;

@Tag(name = "Auth API", description = "로그인, 로그아웃, 토큰 재발급 API, 이메일 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 발급, Refresh토큰은 쿠키에 등록하므로 credential 설정 필수"),
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
    })
    @PostMapping("/login")
    public ResponseEntity<CommonApiResponse<?>> login(@RequestBody @Validated LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse token = authService.login(loginRequest);

        response.setHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh", token.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ofDays(14))
                        .build().toString()
        );

        return ResponseEntity.ok(CommonApiResponse.ok(token));
    }

    @Operation(summary = "Access 토큰 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "재발급 실패, 재로그인 필요")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/reissue")
    public ResponseEntity<CommonApiResponse<?>> reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies == null)
            return ResponseEntity.badRequest().body(CommonApiResponse.error("Refresh 토큰이 없습니다. 다시 로그인 해 주세요."));

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                token = cookie.getValue();
            }
        }
        if (token == null)
            return ResponseEntity.badRequest().body(CommonApiResponse.error("Refresh 토큰이 없습니다. 다시 로그인 해 주세요."));

        LoginResponse reissue = authService.reissue(token);

        response.setHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh", reissue.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ofDays(14))
                        .build().toString()
        );

        return ResponseEntity.ok(CommonApiResponse.ok(reissue));
    }

    @Operation(summary = "로그아웃 API")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<CommonApiResponse<?>> logout(@AuthenticationPrincipal(expression = "username") String username,
                                                       HttpServletResponse response) {
        response.setHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(0)
                        .build().toString()
        );

        authService.logout(username);
        return ResponseEntity.noContent().build(); // 204
    }

    @Operation(summary = "이메일 인증 코드 검증 API")
    @ApiResponses({
            @ApiResponse(responseCode = "410", description = "인증 코드 만료"),
            @ApiResponse(responseCode = "400", description = "인증 코드 불일치")
    })
    @PostMapping("/email/verify")
    public ResponseEntity<CommonApiResponse<?>> emailVerify(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "인증 코드는 6자리 숫자로 입력") @RequestBody @Validated VerifyEmailCodeRequest emailCodeRequest) {

        authService.verifyEmailCode(emailCodeRequest.getEmail(), emailCodeRequest.getCode());

        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }

    @Operation(summary = "인증 메일 요청 API")
    @PostMapping("/email/request-code")
    public ResponseEntity<CommonApiResponse<String>> requestCode(@RequestBody @Validated EmailRequest emailRequest) {

        authService.createEmailCode(emailRequest.getEmail());

        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }

}
