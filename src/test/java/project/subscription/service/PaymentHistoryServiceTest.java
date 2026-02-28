package project.subscription.service;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.response.PaymentHistroyResponse;
import project.subscription.entity.CycleType;
import project.subscription.entity.PaymentHistory;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class PaymentHistoryServiceTest {

    @Autowired
    PaymentHistoryService paymentHistoryService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SubscriptionRepository subscriptionRepository;

    public static final LocalDate BASE_DATE = LocalDate.of(2003, 7, 18);

    @Test
    public void 토탈_머니_정상() throws Exception {
        //given
        User user = initData();

        //when
        PaymentHistroyResponse response = paymentHistoryService.totalMoney(user.getId(), BASE_DATE);

        //then
        assertThat(response.getTotalMoney()).isEqualTo(15000L);
        assertThat(response.getLastPercentage()).isEqualTo(200L);

    }

    @Test
    public void 토탈_머니_예외_존재하지않는유저() throws Exception {
        //then
        assertThatThrownBy(() -> paymentHistoryService.totalMoney(99999999999L, BASE_DATE))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void 토탈_카운트_정상() throws Exception {
        //given
        User user = initData();

        //when
        int response = paymentHistoryService.totalCount(user.getId(), BASE_DATE);

        //then
        assertThat(response).isEqualTo(3);

    }


    @Test
    public void 토탈_카운트_예외_존재하지않는유저() throws Exception {
        //then
        assertThatThrownBy(() -> paymentHistoryService.totalCount(99999999999L, BASE_DATE))
                .isInstanceOf(UserNotFoundException.class);

    }

    private User initData() {
        User user = userRepository.save(User.createLocalUser("sangjae", "sangjae@test.com", "abc123", "ROLE_USER"));
        Subscription subscription = getSubscription();
        Subscription subscription2 = getSubscription();
        Subscription subscription3 = getSubscription();
        Subscription subscription4 = subscriptionRepository.save(new Subscription(
                "Netfilx", "넷플릭스", CycleType.MONTH, 1, BASE_DATE.minusMonths(1), 5000,
                List.of(1, 2, 3), Set.of(
                BASE_DATE.plusMonths(1).minusDays(1),
                BASE_DATE.plusMonths(1).minusDays(2),
                BASE_DATE.plusMonths(1).minusDays(3))
        ));
        user.addSubscription(subscription);
        user.addSubscription(subscription2);
        user.addSubscription(subscription3);
        user.addSubscription(subscription4);
        PaymentHistory paymentHistory = new PaymentHistory(5000, 7);
        PaymentHistory paymentHistory2 = new PaymentHistory(5000, 7);
        PaymentHistory paymentHistory3 = new PaymentHistory(5000, 7);
        PaymentHistory paymentHistory4 = new PaymentHistory(5000, 6);
        user.addPaymentHistory(paymentHistory);
        user.addPaymentHistory(paymentHistory2);
        user.addPaymentHistory(paymentHistory3);
        user.addPaymentHistory(paymentHistory4);
        subscription.addPaymentHistory(paymentHistory);
        subscription2.addPaymentHistory(paymentHistory2);
        subscription3.addPaymentHistory(paymentHistory3);
        subscription4.addPaymentHistory(paymentHistory4);
        return user;
    }

    private Subscription getSubscription() {
        return subscriptionRepository.save(new Subscription(
                "Netfilx", "넷플릭스", CycleType.MONTH, 1, BASE_DATE, 5000,
                List.of(1, 2, 3), Set.of(
                BASE_DATE.plusMonths(1).minusDays(1),
                BASE_DATE.plusMonths(1).minusDays(2),
                BASE_DATE.plusMonths(1).minusDays(3))
        ));
    }
}