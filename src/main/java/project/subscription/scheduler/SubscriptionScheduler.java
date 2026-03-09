package project.subscription.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.service.AlarmService;
import project.subscription.service.PaymentHistoryService;
import project.subscription.service.SubscriptionService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Transactional
public class SubscriptionScheduler {

    private final AlarmService alarmService;
    private final SubscriptionService subscriptionService;
    private final PaymentHistoryService paymentHistoryService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void sendSubscriptionalarms() {
        alarmService.processAlarm(LocalDate.now());
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void updateSubscriptionsForNextCycle() {
        subscriptionService.refreshSubscriptionCycle();
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void deleteHistory() {
        paymentHistoryService.deleteHistory(LocalDate.now().minusMonths(2));
    }

}
