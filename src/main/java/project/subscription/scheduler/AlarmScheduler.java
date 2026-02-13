package project.subscription.scheduler;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.service.AlarmService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final AlarmService alarmService;

    @Transactional
    @Scheduled(cron = "0 0 9 * * *")
    public void alarm() {
        alarmService.processAlarm(LocalDate.now());
    }
}
