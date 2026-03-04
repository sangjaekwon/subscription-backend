package project.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponse {

    private UserTypeAnalysis userTypeAnalysis;
    private List<CoreInterpretation> coreInterpretations;
    private List<AiInsight> aiInsights;
    private OneLineSummary oneLineSummary;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserTypeAnalysis {
        private String title;
        private String description;
        private List<String> tags;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoreInterpretation {
        private String title;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AiInsight {
        private String title;
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OneLineSummary {
        private String summaryTitle;
        private String summaryDescription;
    }
}