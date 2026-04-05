package project.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import project.subscription.aop.annotation.Retry;
import project.subscription.dto.request.EmailRequest;
import project.subscription.dto.request.LoginRequest;
import project.subscription.dto.request.Oauth2LoginRequest;
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


    @Retry(2)
    @Operation(
            summary = "로그인 API",
            description = """
                    아이디와 비밀번호로 로그인합니다.
                    
                    응답 본문에는 Access Token이 포함되며,
                    Refresh Token은 HttpOnly 쿠키(`refresh`)로 함께 발급됩니다.
                    
                    이후 보호된 API 호출 시에는 Access Token을 Authorization Bearer 헤더로 전달해야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공, JWT 토큰 발급, Refresh토큰은 쿠키에 등록하므로 credential 설정 필수",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.access-token",
                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh-token"
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "아이디 또는 비밀번호 입력값이 올바르지 않습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "아이디 또는 비밀번호가 일치하지 않습니다.",
                    content = @Content
            ),
    })
    @PostMapping("/login")
    public ResponseEntity<CommonApiResponse<LoginResponse>> login(@RequestBody @Validated LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse token = authService.login(loginRequest);

        createRereshCookie(response, token);

        return ResponseEntity.ok(CommonApiResponse.ok(token));
    }

    @Operation(
            summary = "Access 토큰 재발급 API",
            description = """
                    HttpOnly 쿠키에 저장된 Refresh Token으로 Access Token을 재발급합니다.
                    
                    요청 본문은 필요하지 않으며, `refresh` 쿠키가 자동으로 포함되어야 합니다.
                    재발급이 성공하면 새로운 Access Token이 응답 본문에 담기고,
                    Refresh Token 쿠키도 함께 갱신됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공, Refresh토큰은 쿠키에 등록하므로 credential 설정 필수",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.new-access-token",
                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9.new-refresh-token"
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh 토큰이 없거나 유효하지 않아 재로그인이 필요합니다.",
                    content = @Content
            )
    })
    @PostMapping("/reissue")
    public ResponseEntity<CommonApiResponse<?>> reissue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies == null)
            return ResponseEntity.status(401).body(CommonApiResponse.error("Refresh 토큰이 없습니다. 다시 로그인 해 주세요."));

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                token = cookie.getValue();
            }
        }
        if (token == null)
            return ResponseEntity.status(401).body(CommonApiResponse.error("Refresh 토큰이 없습니다. 다시 로그인 해 주세요."));

        LoginResponse reissue = authService.reissue(token);

        createRereshCookie(response, reissue);

        return ResponseEntity.ok(CommonApiResponse.ok(reissue));
    }

    @Operation(
            summary = "로그아웃 API",
            description = """
                    현재 로그인 세션을 종료합니다.
                    
                    Authorization 헤더의 Access Token이 필요하며,
                    서버에 저장된 Refresh Token도 함께 무효화합니다.
                    응답 시 브라우저의 `refresh` 쿠키도 만료 처리됩니다.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "NO_CONTENT", content = @Content()),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되어 로그아웃할 수 없습니다.",
                    content = @Content
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonApiResponse<?>> logout(@AuthenticationPrincipal(expression = "userId") Long userId,
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

        authService.logout(userId);
        return ResponseEntity.noContent().build(); // 204
    }

    @Operation(
            summary = "이메일 인증 코드 검증 API",
            description = """
                    이메일로 전송된 6자리 인증 코드를 검증합니다.
                    
                    검증이 성공하면 해당 이메일은 일정 시간 동안 회원가입 가능한 인증 완료 상태가 됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "410",
                    description = "인증 코드 유효 시간이 지나 다시 요청해야 합니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "인증 코드 형식이 잘못되었거나 이메일과 코드가 일치하지 않습니다.",
                    content = @Content
            )
    })
    @PostMapping("/email/verify")
    public ResponseEntity<CommonApiResponse<?>> emailVerify(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "인증 코드는 6자리 숫자로 입력") @RequestBody @Validated VerifyEmailCodeRequest emailCodeRequest) {

        authService.verifyEmailCode(emailCodeRequest.getEmail(), emailCodeRequest.getCode());

        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }

    @Operation(
            summary = "인증 메일 요청 API",
            description = """
                    회원가입 전에 사용할 이메일 인증 코드를 발송합니다.
                    
                    요청한 이메일 주소로 6자리 인증 코드가 전송되며,
                    이후 `/api/auth/email/verify`에서 코드를 검증해야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 메일 요청 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": null
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일 형식이 올바르지 않습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "메일 발송에 실패했습니다.",
                    content = @Content
            )
    })
    @PostMapping("/email/request-code")
    public ResponseEntity<CommonApiResponse<String>> requestCode(@RequestBody @Validated EmailRequest emailRequest) {

        authService.createEmailCode(emailRequest.getEmail());

        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 발급 성공, Refresh토큰은 쿠키에 등록하므로 credential 설정 필수",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "accessToken": "eyJhbGciOiJIUzI1NiJ9.access-token",
                                "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh-token"
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "OAuth2 로그인 완료 후 전달받은 1회성 code가 없거나 유효하지 않습니다.",
                    content = @Content
            )
    })
    @Operation(
            summary = "Oauth2 로그인 이후 토큰 발급 API",
            description = """
                    OAuth2 로그인 성공 후 백엔드가 발급한 1회성 code로 JWT를 발급합니다.
                    
                    응답 본문에는 Access Token이 포함되며
                    Refresh Token은 HttpOnly 쿠키(`refresh`)로 함께 발급됩니다.
                    """
    )
    @PostMapping("/oauth2/login")
    public ResponseEntity<CommonApiResponse<LoginResponse>> loginOauth2(@RequestBody Oauth2LoginRequest loginRequest, HttpServletResponse response) {

        LoginResponse loginResponse = authService.loginOauth2(loginRequest);

        createRereshCookie(response, loginResponse);

        return ResponseEntity.ok(CommonApiResponse.ok(loginResponse));
    }


    private  void createRereshCookie(HttpServletResponse response, LoginResponse reissue) {
        response.setHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh", reissue.getRefreshToken())
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ofDays(14))
                        .build().toString()
        );
    }

}
