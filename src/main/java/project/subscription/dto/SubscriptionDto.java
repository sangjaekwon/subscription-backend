package project.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto implements Serializable {

    @Schema(description = "구독 ID, 생성 시에는 제외", example = "1")
    private Long id;
    @Schema(description = "구독 카테고리", example = "OTT")
    @NotEmpty(message = "카테고리를 선택해 주세요.")
    private String category;
    @Schema(description = "구독 서비스 이름", example = "Netflix")
    @NotEmpty(message = "이름을 작성해 주세요.")
    private String name;
    @Schema(description = "결제 주기", example = "MONTH", allowableValues = {"MONTH", "YEAR"})
    @NotNull(message = "결제 주기를 선택해 주세요.")
    private CycleType paymentCycle; // 달마다인지 년마다인지
    @Schema(description = "결제 주기 간격", example = "1")
    @NotNull(message = "주기를 선택해 주세요.")
    @Min(value = 1, message = "1~11 사이를 입력해 주세요")
    @Max(value = 11, message = "1~11 사이를 입력해 주세요")
    private Integer cycleInterval; // 몇달(년)에 한 번인지
    @Schema(description = "다음 결제일", example = "2026-04-15")
    @NotNull(message = "결제일을 선택해 주세요.")
    private LocalDate dday; // 결제일
    @Schema(description = "결제 금액", example = "17000")
    @NotNull(message = "가격을 입력해 주세요.")
    @Min(value = 0, message = "0보다 큰 숫자를 입력해 주세요.")
    @Max(value = 2100000000, message = "숫자가 너무 큽니다.")
    private Integer price;
    @Schema(description = "알람 없음은 [0], 알람 있음은 [1,3]처럼 N일 전 목록", example = "[1,3]")
    @NotEmpty(message = "알람 주기를 선택해 주세요.")
    private List<Integer> alarm;

    public SubscriptionDto(Subscription subscription) {
        this.paymentCycle = subscription.getPaymentCycle();
        this.id = subscription.getId();
        this.category = subscription.getCategory();
        this.name = subscription.getName();
        this.dday = subscription.getDday();
        this.price = subscription.getPrice();
        this.alarm = subscription.getAlarm();
        this.cycleInterval = subscription.getCycleInterval();
    }
}
