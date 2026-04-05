package project.subscription.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SubscriptionSearchCondition {


    @Schema(description = "정렬 조건", example = "PRICE_DESC", allowableValues = {
            "PRICE_DESC", "NAME_DESC", "DDAY_DESC", "PRICE_ASC", "NAME_ASC", "DDAY_ASC"
    })
    private SubscriptionSortType sortType; // 정렬(이름 , 가격, 결제일)
    @Schema(description = "구독 이름 prefix 검색", example = "Net")
    private String subscriptionName; // 이름 검색
}
