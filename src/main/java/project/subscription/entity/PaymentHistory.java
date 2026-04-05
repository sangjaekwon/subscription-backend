package project.subscription.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory extends BaseTimeEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // JPA 양방향 OneToOne 매핑 이슈(fetch 전략 EAGER) 때문에 ManyToOne unique로 DB 레벨 1:1을 unique로 강제
    @JoinColumn(name = "subscription_id", unique = true)
    private Subscription subscription;

    private Integer price;
    private Integer paymentMonth;

    public PaymentHistory(Integer price, Integer paymentMonth) {
        this.price = price;
        this.paymentMonth = paymentMonth;
    }

    public void changeUser(User user) {
        this.user = user;
    }

    public void updatePaymentHistory(int price, int paymentMonth) {
        this.price = price;
        this.paymentMonth = paymentMonth;
    }

    public void changeSubscription(Subscription subscription) {
        this.subscription = subscription;
    }
}
