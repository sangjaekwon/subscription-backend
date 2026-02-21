package project.subscription.dto.request;


import lombok.Data;

@Data
public class SubscriptionSearchCondition {


    private SubscriptionSortType sortType; // 정렬(이름, 가격, 결제일)
    private String subscriptionName; // 이름 검색
}
