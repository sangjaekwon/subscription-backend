package project.subscription.controller;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.dto.response.PaymentHistroyResponse;
import project.subscription.service.PaymentHistoryService;

import java.time.LocalDate;

@Tag(name = "History API", description = "토탈 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
@SecurityRequirement(name = "bearerAuth")
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;



    @Operation(
            summary = "이번 달 총 결제 금액",
            description = "기준 날짜가 포함된 월의 총 결제 금액을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "totalMoney": 45100,
                                "lastPercentage": 12
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증된 사용자 또는 통계 대상 데이터를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            )
    })
    @GetMapping("/total-money")
    public CommonApiResponse<PaymentHistroyResponse> totalMoney(
            @Parameter(description = "조회 기준 날짜(yyyy-MM-dd)", example = "2026-04-01")
            @RequestParam LocalDate date,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "userId") Long userId) {

        return CommonApiResponse.ok(paymentHistoryService.totalMoney(userId, date));
    }

    @Operation(
            summary = "이번 달 총 결제 건 수",
            description = "기준 날짜가 포함된 월의 총 결제 건 수를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": 3
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증된 사용자 또는 통계 대상 데이터를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            )
    })
    @GetMapping("/total-count")
    public CommonApiResponse<?> totalCount(
            @Parameter(description = "조회 기준 날짜(yyyy-MM-dd)", example = "2026-04-01")
            @RequestParam LocalDate date,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "userId") Long userId) {

        return CommonApiResponse.ok(paymentHistoryService.totalCount(userId, date));
    }

}
