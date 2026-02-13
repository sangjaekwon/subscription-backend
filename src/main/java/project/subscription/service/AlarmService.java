package project.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.subscription.entity.CycleType;
import project.subscription.entity.Subscription;
import project.subscription.entity.User;
import project.subscription.repository.SubscriptionRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final SubscriptionRepository subscriptionRepository;
    private final MailService mailService;

    public void processAlarm(LocalDate today) {
        List<Subscription> targets = subscriptionRepository.findByAlarmDateContaining(today);

        for (Subscription sub : targets) {
            User user = sub.getUser();

            mailService.sendTextMail(
                    user.getEmail(),
                    "구독 결제 알림",
                    sub.getName() + " 결제가 곧 예정되어 있습니다."
            );
            sub.getAlarmDate().remove(today);
            LocalDate alarm = today;
            if (sub.getAlarmDate().isEmpty()) {
                CycleType cycle = sub.getPaymentCycle();

                if (cycle.equals(CycleType.MONTH)) {
                    alarm = alarm.plusMonths(sub.getCycleInterval());
                } else {
                    alarm = alarm.plusYears(sub.getCycleInterval());
                }
                int length = alarm.lengthOfMonth();
                alarm = alarm.withDayOfMonth(Math.min(today.getDayOfMonth(), length));

                List<Integer> alarmList = sub.getAlarm();
                List<LocalDate> alarmDay = sub.getAlarmDate();
                if (!alarmList.isEmpty()) {
                    for (Integer i : alarmList) {
                        alarmDay.add(alarm.minusDays(i));
                    }
                }
            }
        }
    }
}
