package project.subscription.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.SubscriptionDto;
import project.subscription.dto.response.PageResponse;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class SubscriptionServiceTest {

    @Autowired
    SubscriptionService subscriptionService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    EntityManager em;

    public static final LocalDate BASE_DATE = LocalDate.of(2003, 7, 18);


    @Test
    public void 구독_생성_정상() throws Exception {
        //given
        User user = createUser();
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        LocalDate nextPaymentDay = subscriptionDto.getDday();
        while (!nextPaymentDay.isAfter(LocalDate.now())) {
            nextPaymentDay = subscriptionDto.getPaymentCycle().plus
                    (nextPaymentDay, subscriptionDto.getCycleInterval());
        }

        //when
        subscriptionService.saveSubscription(subscriptionDto, user.getId());

        Subscription subscription = subscriptionRepository.findByUser(user).getFirst();
        //then
        assertThat(subscription.getDday()).isEqualTo(nextPaymentDay);
        assertThat(subscription.getAlarmDate()).hasSize(3).containsExactlyInAnyOrder(
                nextPaymentDay.minusDays(1),
                nextPaymentDay.minusDays(2),
                nextPaymentDay.minusDays(3)
        );

    }

    @Test
    public void 구독_생성_예외_존재하지않는유저() throws Exception {
        //given
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        //then
        assertThatThrownBy(() -> subscriptionService.saveSubscription(subscriptionDto, 99999999999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void 구독_수정_정상() throws Exception {
        //given
        User user = createUser();
        Subscription subscription = getSubscription();
        user.addSubscription(subscription);
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        LocalDate nextPaymentDay = subscriptionDto.getDday();
        while (!nextPaymentDay.isAfter(LocalDate.now())) {
            nextPaymentDay = subscriptionDto.getPaymentCycle().plus
                    (nextPaymentDay, subscriptionDto.getCycleInterval());
        }


        //when
        subscriptionDto.setName("넷퓰릭스");
        subscriptionDto.setDday(BASE_DATE.plusDays(1));
        subscriptionService.updateSubscription(subscriptionDto, user.getId(), subscription.getId());

        Subscription updatedSubscription = subscriptionRepository.findByUser(user).getFirst();

        //then
        assertThat(updatedSubscription.getDday()).isEqualTo(nextPaymentDay.plusDays(1));
        assertThat(updatedSubscription.getAlarmDate()).hasSize(3).containsExactlyInAnyOrder(
                nextPaymentDay.plusDays(1).minusDays(1),
                nextPaymentDay.plusDays(1).minusDays(2),
                nextPaymentDay.plusDays(1).minusDays(3)
        );
        assertThat(updatedSubscription.getName()).isEqualTo("넷퓰릭스");
    }

    @Test
    public void 구독_수정_예외_존재하지않는구독정보() throws Exception {
        //given
        User user = createUser();
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        //then
        assertThatThrownBy(() -> subscriptionService.updateSubscription(subscriptionDto, user.getId(), 99999999999L))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }

    @Test
    public void 구독_조회_정상() throws Exception {
        //given
        User user = createUser();
        Subscription subscription1 = getSubscription();
        Subscription subscription2 = getSubscription();
        Subscription subscription3 = getSubscription();
        Subscription subscription4 = getSubscription();
        Subscription subscription5 = getSubscription();
        user.addSubscription(subscription1);
        user.addSubscription(subscription2);
        user.addSubscription(subscription3);
        user.addSubscription(subscription4);
        user.addSubscription(subscription5);

        //when
        PageRequest pageable = PageRequest.of(0, 3);

        PageResponse<SubscriptionDto> subscriptionList = subscriptionService.findSubscriptions(user.getId(), pageable);


        //then
        assertThat(subscriptionList.getPageSize()).isEqualTo(3);
        assertThat(subscriptionList.getTotalPages()).isEqualTo(2);
        assertThat(subscriptionList.getNumberOfElements()).isEqualTo(3);
        assertThat(subscriptionList.getPageNumber()).isEqualTo(0);
        assertThat(subscriptionList.getContent().getFirst().getName()).isEqualTo("넷플릭스");
    }

    @Test
    public void 구독_조회_예외_존재하지않는유저() throws Exception {
        //then
        Pageable pageable = PageRequest.of(0, 5);
        assertThatThrownBy(() -> subscriptionService.findSubscriptions(99999999999L, pageable))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void 구독_삭제_정상() throws Exception {
        //given
        User user = createUser();
        Subscription subscription = getSubscription();
        user.addSubscription(subscription);
        em.flush();
        em.clear();


        //when
        subscriptionService.deleteSubscription(user.getId(), subscription.getId());
        em.flush();
        em.clear();

        Subscription findSubscription = subscriptionRepository.findById(subscription.getId()).orElse(null);
        User findUser = userRepository.findById(user.getId()).get();
        //then
        assertThat(findSubscription).isNull();
        assertThat(findUser.getSubscriptionList()).isEmpty();

    }

    @Test
    public void 구독_삭제_예외_존재하지않는구독정보() throws Exception {
        //given
        User user = createUser();

        //then
        assertThatThrownBy(() -> subscriptionService.deleteSubscription(user.getId(), 99999999999L))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }

    @Test
    public void 구독_기간_조회_정상() throws Exception {
        //given
        User user = createUser();
        Subscription subscription1 = getSubscription(); // 오늘 결제
        Subscription subscription2 = subscriptionRepository.save(new Subscription(
                "Netfilx", "넷플릭스", CycleType.MONTH, 1, LocalDate.now().plusMonths(2), 5000,
                List.of(1, 2, 3), Set.of(
                BASE_DATE.plusMonths(1).minusDays(1),
                BASE_DATE.plusMonths(1).minusDays(2),
                BASE_DATE.plusMonths(1).minusDays(3))
        )); // 2달 뒤 결제
        user.addSubscription(subscription1);
        user.addSubscription(subscription2);

        //when
        int day = 40; // 40일
        PageRequest pageable = PageRequest.of(0, 5);
        PageResponse<SubscriptionDto> subscriptionList = subscriptionService.findSubscriptionsDueSoon(user.getId(), day, pageable);


        //then
        assertThat(subscriptionList.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void 구독_기간_조회_예외_존재하지않는유저() throws Exception {
        //then
        assertThatThrownBy(() -> subscriptionService.findSubscriptionsDueSoon(99999999999L, 1, null))
                .isInstanceOf(UserNotFoundException.class);
    }

    private User createUser() {
        return userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
    }

    @Test
    public void 결제_주기_및_알람_리프레시_정상() throws Exception {
        //given


        //when


        //then

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

    private static SubscriptionDto getSubscriptionDto() {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setAlarm(List.of(1, 2, 3));
        subscriptionDto.setName("넷플릭스");
        subscriptionDto.setCategory("Netflix");
        subscriptionDto.setPrice(5000);
        subscriptionDto.setPaymentCycle(CycleType.MONTH);
        subscriptionDto.setCycleInterval(1);
        subscriptionDto.setDday(BASE_DATE);
        return subscriptionDto;
    }

}