package project.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponse {

    @Schema(description = "사용자 유형 분석 결과")
    private UserTypeAnalysis userTypeAnalysis;
    @Schema(description = "핵심 해석 3개")
    private List<CoreInterpretation> coreInterpretations;
    @Schema(description = "실행 가능한 AI 인사이트 목록")
    private List<AiInsight> aiInsights;
    @Schema(description = "한 줄 요약")
    private OneLineSummary oneLineSummary;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserTypeAnalysis {
        @Schema(description = "사용자 유형명", example = "실속형 스트리밍 사용자")
        private String title;
        @Schema(description = "사용자 유형 설명", example = "필요한 서비스만 유지하면서도 콘텐츠 소비 패턴이 뚜렷한 편입니다.")
        private String description;
        @Schema(description = "유형 태그", example = "[\"실속형\", \"OTT중심\", \"정기결제\"]")
        private List<String> tags;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoreInterpretation {
        @Schema(description = "핵심 해석 제목", example = "OTT 비중이 높은 구독 구조")
        private String title;
        @Schema(description = "핵심 해석 설명", example = "콘텐츠 소비 중심 구독이 많아 생활 편의 서비스보다 여가 지출 비중이 더 높습니다.")
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiInsight {
        @Schema(description = "AI 인사이트 제목", example = "겹치는 콘텐츠 구독 점검")
        private String title;
        @Schema(description = "AI 인사이트 설명", example = "비슷한 용도의 OTT를 동시에 구독 중이라면 사용 빈도를 기준으로 정리해볼 만합니다.")
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OneLineSummary {
        @Schema(description = "한 줄 요약 제목", example = "필요한 만큼만 쓰는 콘텐츠 중심형")
        private String summaryTitle;
        @Schema(description = "한 줄 요약 설명", example = "구독 수는 많지 않지만 사용 목적이 뚜렷해 지출 통제가 비교적 잘 되는 편입니다.")
        private String summaryDescription;
    }
}
