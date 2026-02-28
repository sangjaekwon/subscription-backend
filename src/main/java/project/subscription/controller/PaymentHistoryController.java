package project.subscription.controller;


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


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저 혹은 구독 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.")
    })
    @GetMapping("/total-money")
    public CommonApiResponse<PaymentHistroyResponse> totalMoney(@RequestParam LocalDate date
            , @AuthenticationPrincipal(expression = "userId") Long userId) {

        return CommonApiResponse.ok(paymentHistoryService.totalMoney(userId, date));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저 혹은 구독 정보를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "401", description = "인증에 실패했습니다.")
    })
    @GetMapping("/total-count")
    public CommonApiResponse<?> totalCount(@RequestParam LocalDate date
            , @AuthenticationPrincipal(expression = "userId") Long userId) {

        return CommonApiResponse.ok(paymentHistoryService.totalCount(userId, date));
    }

}
