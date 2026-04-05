package project.subscription.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name= "OAuth2 소셜 로그인", description = "소셜 로그인 엔드포인트")
@RestController
public class OAuth2SwaggerController {

    @Operation(
            summary = "Google OAuth2 로그인",
            description = """
                    Google OAuth2 로그인 시작 엔드포인트입니다.
                    
                    실제 처리는 Spring Security OAuth2가 담당하며,
                    호출 시 Google 로그인 페이지로 리다이렉트됩니다.
                    
                    Swagger에서는 리다이렉트 흐름을 정상 완료하기 어렵기 때문에
                    문서 확인용 엔드포인트로만 사용하세요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Google 로그인 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "400", description = "Swagger 문서용 엔드포인트")
    })
    @GetMapping("/oauth2/authorization/google")
    public void googleLogin() throws BadRequestException {
        throw new BadRequestException("Swagger 문서용 엔드포인트입니다.");
    }

    @Operation(
            summary = "Naver OAuth2 로그인",
            description = """
                    Naver OAuth2 로그인 시작 엔드포인트입니다.
                    
                    실제 처리는 Spring Security OAuth2가 담당하며,
                    호출 시 Naver 로그인 페이지로 리다이렉트됩니다.
                    
                    로그인 완료 후 프론트는 백엔드가 전달한 1회성 code를
                    `/api/auth/oauth2/login`으로 보내 JWT를 발급받습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Naver 로그인 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "400", description = "Swagger 문서용 엔드포인트")
    })
    @GetMapping("/oauth2/authorization/naver")
    public void naverLogin() throws BadRequestException {
        throw new BadRequestException("Swagger 문서용 엔드포인트입니다.");
    }

    @Operation(
            summary = "Kakao OAuth2 로그인",
            description = """
                    Kakao OAuth2 로그인 시작 엔드포인트입니다.
                    
                    실제 처리는 Spring Security OAuth2가 담당하며,
                    호출 시 Kakao 로그인 페이지로 리다이렉트됩니다.
                    
                    로그인 완료 후 프론트는 백엔드가 전달한 1회성 code를
                    `/api/auth/oauth2/login`으로 보내 JWT를 발급받습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Kakao 로그인 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "400", description = "Swagger 문서용 엔드포인트")
    })
    @GetMapping("/oauth2/authorization/kakao")
    public void kakaoLogin() throws BadRequestException {
        throw new BadRequestException("Swagger 문서용 엔드포인트입니다.");
    }
}
