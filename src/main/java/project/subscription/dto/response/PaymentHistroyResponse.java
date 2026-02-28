package project.subscription.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentHistroyResponse {

    private Long totalMoney;
    private Long lastPercentage;
}
