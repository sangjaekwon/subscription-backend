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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.request.SubscriptionSearchCondition;
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

    @Operation(
            summary = "구독 목록 조회",
            description = """
                    사용자의 구독 목록을 페이징 조회합니다.
                    
                    pageable 파라미터 예시:
                    - page: 0부터 시작하는 페이지 번호
                    - size: 페이지 크기
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "content": [
                                  {
                                    "id": 1,
                                    "category": "OTT",
                                    "name": "Netflix",
                                    "paymentCycle": "MONTH",
                                    "cycleInterval": 1,
                                    "dday": "2026-04-15",
                                    "price": 17000,
                                    "alarm": [1, 3]
                                  }
                                ],
                                "totalElements": 1,
                                "totalPages": 1,
                                "pageSize": 10,
                                "pageNumber": 0,
                                "numberOfElements": 1
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증된 사용자를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<CommonApiResponse<PageResponse<SubscriptionDto>>> getSubscription(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "userId") Long userId, Pageable pageable) {
        return ResponseEntity.ok(CommonApiResponse.ok(subscriptionService.findPageSubscriptions(userId, pageable)));
    }

    @Operation(
            summary = "구독 목록 조회 필터링",
            description = """
                    구독 이름 검색과 정렬 조건을 함께 사용해 구독 목록을 조회합니다.
                    
                    정렬 값:
                    - PRICE_DESC, PRICE_ASC
                    - NAME_DESC, NAME_ASC
                    - DDAY_DESC, DDAY_ASC
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "content": [
                                  {
                                    "id": 1,
                                    "category": "OTT",
                                    "name": "Netflix",
                                    "paymentCycle": "MONTH",
                                    "cycleInterval": 1,
                                    "dday": "2026-04-15",
                                    "price": 17000,
                                    "alarm": [1, 3]
                                  }
                                ],
                                "totalElements": 1,
                                "totalPages": 1,
                                "pageSize": 10,
                                "pageNumber": 0,
                                "numberOfElements": 1
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증된 사용자를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            )
    })
    @GetMapping("/filter")
    public ResponseEntity<CommonApiResponse<PageResponse<SubscriptionDto>>> getSubscriptionfilter(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @Parameter(description = "정렬 조건과 구독 이름 검색 조건")
            SubscriptionSearchCondition searchCondition,
            Pageable pageable) {
        return ResponseEntity.ok(CommonApiResponse.ok(subscriptionService.findFilterSubscriptions(userId, searchCondition, pageable)));
    }

    @Operation(summary = "결제 기간 임박 구독 목록 조회", description = "결제일 기준 day일전 구독 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "결제일 기반 구독 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "content": [
                                  {
                                    "id": 2,
                                    "category": "MUSIC",
                                    "name": "Spotify",
                                    "paymentCycle": "MONTH",
                                    "cycleInterval": 1,
                                    "dday": "2026-04-08",
                                    "price": 10900,
                                    "alarm": [1]
                                  }
                                ],
                                "totalElements": 1,
                                "totalPages": 1,
                                "pageSize": 10,
                                "pageNumber": 0,
                                "numberOfElements": 1
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "day 값이 올바르지 않습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증된 사용자를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            )
    })
    @GetMapping("/due")
    public ResponseEntity<CommonApiResponse<PageResponse<SubscriptionDto>>> getSubscriptionDue(
            @Parameter(description = "오늘 기준 N일 이내 결제 예정 구독을 조회합니다.", example = "7")
            @RequestParam int day, Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ResponseEntity.ok(CommonApiResponse.ok(
                subscriptionService.findSubscriptionsDueSoon(userId, day, pageable)));
    }

    @Operation(summary = "구독 정보 저장")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "구독 정보 저장 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수값 누락, 형식 오류, 또는 alarm 규칙이 올바르지 않습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "인증된 사용자를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<CommonApiResponse<?>> saveSubscription(
            @Validated @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "id는 제거해서 요청, paymentCycle은 MONTH 또는 YEAR, alarm은 [0] 또는 [1,3] 형태로 요청")
            @RequestBody SubscriptionDto subscriptionDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.saveSubscription(subscriptionDto, userId);
        return ResponseEntity.status(201).body(CommonApiResponse.ok(null));
    }

    @Operation(summary = "구독 정보 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "구독 정보 삭제 성공", content = @Content()),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제 대상 구독을 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "다른 사용자의 구독 정보에는 접근할 수 없습니다.",
                    content = @Content
            )
    })
    @DeleteMapping
    public ResponseEntity<CommonApiResponse<List<?>>> deleteSubscription
            (@Parameter(description = "삭제할 구독 ID", example = "1")
             @RequestParam Long subscriptionId,
             @Parameter(hidden = true)
             @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.deleteSubscription(userId, subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "구독 정보 수정")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "구독 정보 수정 성공",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수값 누락, 형식 오류, 또는 alarm 규칙이 올바르지 않습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정 대상 구독 또는 사용자를 찾을 수 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "다른 사용자의 구독 정보에는 접근할 수 없습니다.",
                    content = @Content
            )
    })
    @PutMapping
    public ResponseEntity<CommonApiResponse<List<?>>> updateSubscription
            (@RequestBody @Validated SubscriptionDto subscriptionDto,
             @Parameter(hidden = true)
             @AuthenticationPrincipal(expression = "userId") Long userId) {
        subscriptionService.updateSubscription(subscriptionDto, userId);
        return ResponseEntity.ok(CommonApiResponse.ok(null));
    }


}
