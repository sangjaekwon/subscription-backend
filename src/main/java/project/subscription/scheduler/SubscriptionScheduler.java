package project.subscription.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.service.AlarmService;
import project.subscription.service.SubscriptionService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Transactional
public class SubscriptionScheduler {

    private final AlarmService alarmService;
    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendSbubscriptionalarms() {
        alarmService.processAlarm(LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void updateSubscriptionsForNextCycle() {
        subscriptionService.refreshSubscriptionCycle();
    }

}
