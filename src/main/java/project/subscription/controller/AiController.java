package project.subscription.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.subscription.aop.annotation.Retry;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.response.AiResponse;
import project.subscription.dto.response.CommonApiResponse;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.service.SubscriptionService;

import java.util.List;

@Tag(name = "AI 분석 API")
@RestController
@SecurityRequirement(name = "bearerAuth")
@Profile({"dev", "prod"})
@RequestMapping("/api/ai")
public class AiController {

    private final ChatClient chatClient;
    private final SubscriptionService subscriptionService;

    public AiController(ChatClient.Builder chatClient, SubscriptionService subscriptionService) {
        this.chatClient = chatClient.build();
        this.subscriptionService = subscriptionService;
    }
    public static final String SYSTEM_PROMPT = """
    당신은 구독 소비 패턴을 분석하는 AI 분석가입니다.
    
    사용자가 제공한 구독 서비스 목록을 바탕으로, 사용자의 구독 성향과 소비 패턴을 분석하세요.
    
    반드시 아래 규칙을 지키세요.
    
    [역할]
    - 당신은 사용자의 현재 구독 상태를 보고 소비 성향을 분석하는 전문가입니다.
    - 단순 나열이 아니라 "왜 이런 유형인지"를 자연스럽게 설명해야 합니다.
    
    [출력 목표]
    출력은 반드시 JSON 객체 하나만 반환하세요.
    절대 JSON 바깥에 설명, 마크다운, 코드블록, 안내문, 머리말, 꼬리말을 추가하지 마세요.
    
    [데이터 해석 규칙]
    - 서비스 ID, 내부 식별자, DB 번호 같은 내부 정보는 절대 언급하지 마세요.
    - 서비스 이름이 비어있거나 알 수 없으면 "특정 구독 서비스"처럼 일반적인 표현을 사용하세요.
    
    [분석 규칙]
    - 사용자의 구독 서비스 이름, 카테고리, 가격, 결제 주기, 결제일 등의 정보가 주어질 수 있습니다.
    - 제공된 정보만 바탕으로 분석하세요.
    - 정보가 부족한 부분은 과장하지 말고, 현재 데이터 기준으로 자연스럽게 해석하세요.
    - 문장은 사용자에게 보여주는 UI용 문구처럼 자연스럽고 읽기 쉽게 작성하세요.
    - 지나치게 딱딱하거나 기계적인 문장은 피하세요.
    - 부정적인 판단보다는 "점검", "정리", "최적화", "확인" 같은 표현을 우선 사용하세요.
    
    [반드시 포함할 구조]
    1. 사용자 유형 분석
       - 유형명(title)
       - 유형 설명(description)
       - 유형 태그(tags) 2~5개
    
    2. 핵심 해석 3개
       - 반드시 3개만 반환
       - 각 항목은 title, description 포함
    
    3. AI 인사이트 2개 이상
       - 최소 2개, 최대 3개
       - 각 항목은 title, description 포함
    
    4. 한 줄 요약
       - summaryTitle
       - summaryDescription
    
    [스타일 규칙]
    - title은 짧고 인상적으로 작성
    - description은 2~4문장 이내
    - tags는 짧은 명사형/형용사형 표현으로 작성
    - 핵심 해석은 서로 겹치지 않게 작성
    - AI 인사이트는 실제로 사용자가 점검하거나 행동할 수 있는 제안 중심으로 작성
    - 한 줄 요약은 사용자의 성향을 한 문장으로 압축한 느낌이어야 함
    - 반드시 유효한 JSON만 반환
    - 응답 시작은 { 로 시작하고, 응답 끝은 } 로 끝나야 함
    - 문자열은 모두 큰따옴표(")를 사용하세요.
    - 후행 쉼표를 넣지 마세요.
    
    [출력 스키마]
    반드시 아래 JSON 구조를 따르세요.
    서비스 ID, 내부 식별자, DB 번호(P 같은 내부 정보는 절대 언급하지 마세요.
    
    {
      "userTypeAnalysis": {
        "title": "string",
        "description": "string",
        "tags": ["string", "string"]
      },
      "coreInterpretations": [
        {
          "title": "string",
          "description": "string"
        },
        {
          "title": "string",
          "description": "string"
        },
        {
          "title": "string",
          "description": "string"
        }
      ],
      "aiInsights": [
        {
          "title": "string",
          "description": "string"
        }
      ],
      "oneLineSummary": {
        "summaryTitle": "string",
        "summaryDescription": "string"
      }
    }
    """;

    public static final String USER_PROMPT = """
    다음은 사용자 구독 데이터입니다.
    구독 목록(JSON):
    {subscriptionData}
    
    위 데이터를 바탕으로 사용자의 구독 성향을 분석해주세요.
    
    요구사항:
    - 사용자 유형 분석 포함
    - 핵심 해석 3개 고정
    - AI 인사이트 2개 이상
    - 한 줄 요약 포함
    - 반드시 JSON만 반환
    """;

    @Retry() // default 3
    @Operation(
            summary = "AI 분석 API",
            description = """
                    사용자의 현재 구독 목록을 기반으로 소비 성향을 분석합니다.
                    
                    내부 AI 호출 실패 시 `@Retry` 정책에 따라 최대 3번까지 재시도합니다.
                    구독 데이터가 하나도 없으면 분석할 수 없습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "AI 분석 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "success": true,
                              "data": {
                                "userTypeAnalysis": {
                                  "title": "실속형 스트리밍 사용자",
                                  "description": "필요한 서비스만 유지하면서도 콘텐츠 소비 패턴이 뚜렷한 편입니다.",
                                  "tags": ["실속형", "OTT중심", "정기결제"]
                                },
                                "coreInterpretations": [
                                  {
                                    "title": "OTT 비중이 높은 구독 구조",
                                    "description": "콘텐츠 소비 중심 구독이 많아 여가 지출 비중이 높은 편입니다."
                                  },
                                  {
                                    "title": "월 단위 관리가 쉬운 구성",
                                    "description": "대부분 월간 결제로 구성되어 지출 흐름을 파악하기 쉽습니다."
                                  },
                                  {
                                    "title": "비용 대비 사용성 점검 여지",
                                    "description": "사용 빈도가 낮은 서비스가 있다면 정리 효과가 바로 드러날 수 있습니다."
                                  }
                                ],
                                "aiInsights": [
                                  {
                                    "title": "겹치는 콘텐츠 구독 점검",
                                    "description": "비슷한 용도의 OTT를 동시에 구독 중이라면 사용 빈도를 기준으로 정리해볼 만합니다."
                                  },
                                  {
                                    "title": "결제일 집중도 확인",
                                    "description": "결제일이 비슷하게 몰려 있다면 월초나 월말 지출 체감이 커질 수 있어 분산을 고려해볼 수 있습니다."
                                  }
                                ],
                                "oneLineSummary": {
                                  "summaryTitle": "필요한 만큼만 쓰는 콘텐츠 중심형",
                                  "summaryDescription": "구독 수는 많지 않지만 사용 목적이 분명해 지출 통제가 비교적 잘 되는 편입니다."
                                }
                              }
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Access 토큰이 없거나 만료되었습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "분석할 구독 데이터가 없습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "외부 AI 호출이 반복 실패해 분석을 완료하지 못했습니다.",
                    content = @Content
            )
    })
    @GetMapping
    public CommonApiResponse<AiResponse> aiResult(@Parameter(hidden = true) @AuthenticationPrincipal(expression = "userId") Long userId) {

        List<SubscriptionDto> subscriptions = subscriptionService.findSubscriptions(userId);
        if (subscriptions.isEmpty()) throw new SubscriptionNotFoundException();

        AiResponse res = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u.text(USER_PROMPT).param("subscriptionData", subscriptions ))
                .call()
                .entity(AiResponse.class);

        return CommonApiResponse.ok(res);
    }
}
