package project.subscription.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentHistroyResponse {

    @Schema(description = "기준 월 총 결제 금액", example = "45100")
    private Long totalMoney;
    @Schema(description = "전월 대비 증감률", example = "12")
    private Long lastPercentage;
}
