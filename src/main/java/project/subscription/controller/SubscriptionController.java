package project.subscription.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.dto.response.PageResponse;
import project.subscription.service.SubscriptionService;

import java.util.List;

@Tag(name = "Subscription API", description = "구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "구독 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구독 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.")
    })
    @GetMapping
    public ResponseEntity<CommonApiResponse<PageResponse<SubscriptionDto>>> getSubscription(
            @AuthenticationPrincipal(expression = "userId") Long userId, Pageable pageable) {
        return ResponseEntity.ok(CommonApiResponse.ok(subscriptionService.findSubscriptions(userId, pageable)));
    }

    @Operation(summary = "결제 기간 임박 구독 목록 조회", description = "결제일 기준 day일전 구독 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제일 기반 구독 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.")
    })
    @GetMapping("/due")
    public ResponseEntity<CommonApiResponse<PageResponse<SubscriptionDto>>> getSubscriptionDue(
            @RequestParam int day, Pageable pageable,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ResponseEntity.ok(CommonApiResponse.ok(
                subscriptionService.findSubscriptionsDueSoon(userId, day, pageable)));
    }

    @Operation(summary = "구독 정보 저장")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "구독 정보 저장 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없습니다.")
    })
    @PostMapping
    public ResponseEntity<CommonApiResponse<?>> saveSubscription(
            @Validated @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "id는 제거해서 요청, paymentCycle은 MONTH, YEAR로 요청, alarm은 숫자로 입력(1,2  -> 1일전, 2일전)")
            @RequestBody SubscriptionDto subscriptionDto,
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.saveSubscription(subscriptionDto, userId);
        return ResponseEntity.status(201).body(CommonApiResponse.ok(null));
    }

    @Operation(summary = "구독 정보 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "구독 정보 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "구독 정보를 찾을 수 없습니다.")
    })
    @DeleteMapping
    public ResponseEntity<CommonApiResponse<List<?>>> deleteSubscription
            (@RequestParam Long subscriptionId, @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.deleteSubscription(userId, subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "구독 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구독 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "구독 혹은 유저 정보를 찾을 수 없습니다.")
    })
    @PutMapping
    public ResponseEntity<CommonApiResponse<List<?>>> updateSubscription
            (@RequestBody SubscriptionDto subscriptionDto, @RequestParam Long subscriptionId,
             @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.updateSubscription(subscriptionDto, userId, subscriptionId);
        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }


}
