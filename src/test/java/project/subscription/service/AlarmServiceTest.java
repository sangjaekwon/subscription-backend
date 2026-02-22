package project.subscription.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.repository.SubscriptionRepository;
import project.subscription.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AlarmServiceTest {

    @Autowired
    AlarmService alarmService;
    @Autowired
    SubscriptionRepository subscriptionRepository;
    @Autowired
    UserRepository userRepository;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    public static final LocalDate BASE_DATE = LocalDate.of(2003, 7, 18);


    @Test
    public void 결제알람_전송_정상() throws Exception {
        //given
        User user = createUser();
        Subscription subscription = getSubscription();
        user.addSubscription(subscription);
        subscriptionRepository.saveAndFlush(subscription);

        //when
        alarmService.processAlarm(BASE_DATE.minusDays(1));
        MimeMessage[] messages = greenMail.getReceivedMessages();

        //then
        assertThat(messages.length).isEqualTo(1);


    }

    private Subscription getSubscription() {
        return subscriptionRepository.save(new Subscription(
                "Netfilx", "넷플릭스", CycleType.MONTH, 1, BASE_DATE, 5000,
                List.of(1, 2, 3), Set.of(
                BASE_DATE.minusDays(1),
                BASE_DATE.minusDays(2),
                BASE_DATE.minusDays(3))
        ));
    }

    private User createUser() {
        return userRepository.save(User.createLocalUser(UUID.randomUUID().toString(), "sjsj00718@gmail.com", "abc123!", "ROLE_USER"));
    }

}