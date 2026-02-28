package project.subscription.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    private String category;
    private String name;

    @Enumerated(EnumType.STRING)
    private CycleType paymentCycle; // 달마다인지 년마다인지
    private Integer cycleInterval; // 몇달(년)에 한 번인지
    private LocalDate dday; // 결제일

    private Integer price;

    private List<Integer> alarm; // 알람 언제 할 건지
    @Convert(converter = LocalDateSetConverter.class)
    private Set<LocalDate> alarmDate; // 알람이 일어날 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentHistory paymentHistory;

    public Subscription(String category, String name, CycleType paymentCycle, Integer cycleInterval,
                        LocalDate dday, Integer price, List<Integer> alarm, Set<LocalDate> alarmDate) {
        this.category = category;
        this.name = name;
        this.paymentCycle = paymentCycle;
        this.cycleInterval = cycleInterval;
        this.dday = dday;
        this.price = price;
        this.alarm = alarm;
        this.alarmDate = alarmDate;
    }

    public void updateSubscription(String category, String name, CycleType paymentCycle, Integer cycleInterval,
                                   LocalDate dday, Integer price, List<Integer> alarm, Set<LocalDate> alarmDate) {
        this.category = category;
        this.name = name;
        this.paymentCycle = paymentCycle;
        this.cycleInterval = cycleInterval;
        this.dday = dday;
        this.price = price;
        this.alarm = alarm;
        this.alarmDate = alarmDate;
    }


    public void changeUser(User user) {
        this.user = user;
    }

    public void refreshPaymentDay(LocalDate dday, Set<LocalDate> alarmDate) {
        this.dday = dday;
        this.alarmDate = alarmDate;
    }

    public void addPaymentHistory(PaymentHistory paymentHistory) {
        this.paymentHistory = paymentHistory;
        paymentHistory.changeSubscription(this);
    }
}
