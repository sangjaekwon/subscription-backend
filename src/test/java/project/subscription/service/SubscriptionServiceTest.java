package project.subscription.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.SubscriptionDto;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.exception.ex.SubscriptionNotFoundException;
import project.subscription.exception.ex.UserNotFoundException;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


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


    @Test
    public void 구독_생성_정상() throws Exception {
        //given
        User user = userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
        SubscriptionDto subscriptionDto = getSubscriptionDto();


        //when
        subscriptionService.saveSubscription(subscriptionDto, user.getId());

        Subscription subscription = subscriptionRepository.findByUser(user).getFirst();
        //then
        assertThat(subscription.getDday()).isEqualTo(LocalDate.now().plusMonths(1));
        assertThat(subscription.getAlarmDate()).hasSize(3).containsExactlyInAnyOrder(
                LocalDate.now().plusMonths(1).minusDays(1),
                LocalDate.now().plusMonths(1).minusDays(2),
                LocalDate.now().plusMonths(1).minusDays(3)
        );

    }
    @Test
    public void 구독_생성_예외_존재하지않는유저() throws Exception {
        //given
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        //then
        assertThatThrownBy(()-> subscriptionService.saveSubscription(subscriptionDto, 99999999999L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void 구독_수정_정상() throws Exception {
        //given
        User user = userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
        Subscription subscription = getSubscription();
        user.addSubscription(subscription);
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        //when
        subscriptionDto.setName("넷퓰릭스");
        subscriptionDto.setDday(LocalDate.now().plusDays(1));
        subscriptionService.updateSubscription(subscriptionDto, user.getId(), subscription.getId());

        Subscription updatedSubscription = subscriptionRepository.findByUser(user).getFirst();

        //then
        assertThat(updatedSubscription.getDday()).isEqualTo(LocalDate.now().plusDays(1).plusMonths(1));
        assertThat(subscription.getAlarmDate()).hasSize(3).containsExactlyInAnyOrder(
                LocalDate.now().plusDays(1).plusMonths(1).minusDays(1),
                LocalDate.now().plusDays(1).plusMonths(1).minusDays(2),
                LocalDate.now().plusDays(1).plusMonths(1).minusDays(3)
        );
        assertThat(updatedSubscription.getName()).isEqualTo("넷퓰릭스");
    }

    @Test
    public void 구독_수정_예외_존재하지않는구독정보() throws Exception {
        //given
        User user = userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
        SubscriptionDto subscriptionDto = getSubscriptionDto();

        //then
        assertThatThrownBy(()-> subscriptionService.updateSubscription(subscriptionDto, user.getId(), 99999999999L))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }

    @Test
    public void 구독_조회_정상() throws Exception {
        //given
        User user = userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
        Subscription subscription1 = getSubscription();
        Subscription subscription2 = getSubscription();
        user.addSubscription(subscription1);
        user.addSubscription(subscription2);

        //when
        List<SubscriptionDto> subscriptionList = subscriptionService.getSubscriptions(user.getId());


        //then
        assertThat(subscriptionList).hasSize(2);
    }
    @Test
    public void 구독_조회_예외_존재하지않는유저() throws Exception {
        //then
        assertThatThrownBy(()-> subscriptionService.getSubscriptions(99999999999L))
                .isInstanceOf(UserNotFoundException.class);
    }
    @Test
    public void 구독_삭제_정상() throws Exception {
        //given
        User user = userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
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
        User user = userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));

        //then
        assertThatThrownBy(()-> subscriptionService.deleteSubscription(user.getId(), 99999999999L))
                .isInstanceOf(SubscriptionNotFoundException.class);
    }



    private Subscription getSubscription() {
        return subscriptionRepository.save(new Subscription(
                "Netfilx", "넷플릭스", CycleType.MONTH, 1, LocalDate.now(), 5000,
                List.of(1, 2, 3), List.of(
                LocalDate.now().plusDays(1).plusMonths(1).minusDays(1),
                LocalDate.now().plusDays(1).plusMonths(1).minusDays(2),
                LocalDate.now().plusDays(1).plusMonths(1).minusDays(3))
        ));
    }

    private static SubscriptionDto getSubscriptionDto() {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setAlarm(List.of(1,2,3));
        subscriptionDto.setName("넷플릭스");
        subscriptionDto.setDday(LocalDate.now().plusMonths(1));
        subscriptionDto.setCategory("Netflix");
        subscriptionDto.setPrice(5000);
        subscriptionDto.setPaymentCycle(CycleType.MONTH);
        subscriptionDto.setCycleInterval(1);
        subscriptionDto.setDday(LocalDate.now());
        return subscriptionDto;
    }

}