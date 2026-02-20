package project.subscription.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.request.SubscriptionDueRequest;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.service.SubscriptionService;

import java.util.List;

@Tag(name="Subscription API", description = "구독 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
@SecurityRequirement(name ="bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "구독 목록 조회")
    @GetMapping
    public ResponseEntity<CommonApiResponse<List<SubscriptionDto>>> getSubscription(@AuthenticationPrincipal(expression = "userId") Long userId) {
        return ResponseEntity.ok(CommonApiResponse.ok(subscriptionService.getSubscriptions(userId)));
    }

    @Operation(summary = "결제 기간 임박 구독 목록 조회", description = "결제일 기준 day일전 구독 목록 조회")
    @GetMapping("/due")
    public ResponseEntity<CommonApiResponse<List<SubscriptionDto>>> getSubscriptionDue(@RequestBody SubscriptionDueRequest subscriptionDueRequest, @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ResponseEntity.ok(CommonApiResponse.ok(subscriptionService.getSubscriptionsDueSoon(userId, subscriptionDueRequest.getDay())));
    }

    @Operation(summary = "구독 정보 저장")
    @PostMapping
    public ResponseEntity<CommonApiResponse<?>> saveSubscription(@Validated @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "id는 제거해서 요청, paymentCycle은 MONTH, YEAR로 요청") @RequestBody SubscriptionDto subscriptionDto, @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.saveSubscription(subscriptionDto, userId);
        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }

    @Operation(summary = "구독 정보 삭제")
    @DeleteMapping
    public ResponseEntity<CommonApiResponse<List<?>>> deleteSubscription(@RequestParam Long subscriptionId, @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.deleteSubscription(userId, subscriptionId);
        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }

    @Operation(summary = "구독 정보 수정")
    @PutMapping
    public ResponseEntity<CommonApiResponse<List<?>>> updateSubscription(@RequestBody SubscriptionDto subscriptionDto, @RequestParam Long subscriptionId, @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.updateSubscription(subscriptionDto, userId, subscriptionId);
        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }




}
