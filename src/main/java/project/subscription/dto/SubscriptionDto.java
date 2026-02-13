package project.subscription.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class SubscriptionDto {

    private Long id;
    @NotEmpty
    private String category;
    @NotEmpty
    private String name;
    @NotEmpty
    private CycleType paymentCycle; // 달마다인지 년마다인지
    @NotEmpty
    private Integer cycleInterval; // 몇달(냔)에 한 번인지
    @NotEmpty
    private LocalDate dday; // 결제일
    @NotEmpty
    private Integer price;
    @NotEmpty
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
