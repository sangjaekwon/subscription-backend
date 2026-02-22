package project.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.repository.SubscriptionRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private final SubscriptionRepository subscriptionRepository;
    private final MailService mailService;
    private final TemplateEngine templateEngine;


    public void processAlarm(LocalDate today) {
        List<Subscription> targets = subscriptionRepository.findByAlarmDateContaining(today.toString());


        for (Subscription sub : targets) {
            User user = sub.getUser();

            long daysLeft = ChronoUnit.DAYS.between(today, sub.getDday());

            Context context = new Context();
            context.setVariable("daysLeft", daysLeft);
            context.setVariable("subscriptionName", sub.getName());
            context.setVariable("price", sub.getPrice());
            context.setVariable("dday", sub.getDday());

            String html = templateEngine.process("alarm", context);

            mailService.sendMail(user.getEmail(), html,
                    "⏰ [" + daysLeft + "일 후] " + sub.getName() + " 구독 결제 예정 안내 – 구독관리서비스.site");

            sub.getAlarmDate().remove(today);
        }
    }
}
