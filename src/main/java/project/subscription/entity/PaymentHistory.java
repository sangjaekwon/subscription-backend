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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_Id")
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
